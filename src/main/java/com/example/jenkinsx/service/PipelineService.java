package com.example.jenkinsx.service;

import com.example.jenkinsx.dto.*;
import com.example.jenkinsx.entity.*;
import com.example.jenkinsx.repository.PipelineHistoryRepository;
import com.example.jenkinsx.repository.PipelineRepository;
import com.example.jenkinsx.repository.UserRepository;
import com.example.jenkinsx.executor.SSHExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import com.example.jenkinsx.util.SSHKeyUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PipelineService {

    private final PipelineRepository pipelineRepository;
    private final UserRepository userRepository;
    private final PipelineHistoryRepository historyRepository;
    private final TransactionTemplate transactionTemplate;

    public PipelineService(PipelineRepository pipelineRepository,
                           UserRepository userRepository, PipelineHistoryRepository historyRepository,
                           TransactionTemplate transactionTemplate) {
        this.pipelineRepository = pipelineRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        this.transactionTemplate = transactionTemplate;
    }

    private String normalizeRepo(String url) {
        if (url == null) return null;
        return url.replace(".git", "").trim();
    }

    private String substituteSecrets(String command, List<Secret> secrets) {
        if (secrets == null || command == null) return command;
        String substituted = command;
        for (Secret secret : secrets) {
            String token = "$" + secret.getSecretName();
            if (substituted.contains(token)) {
                substituted = substituted.replace(token, secret.getSecretContent());
            }
        }
        return substituted;
    }

    private String extractRepoName(String url) {
        if (url == null || url.trim().isEmpty()) return null;
        String[] parts = url.split("/");
        String last = parts[parts.length - 1];
        return last.replace(".git", "");
    }

    public Pipeline addPipeline(AddPipeline dto) {
        User user = userRepository.findById(dto.getUserId()).orElseThrow();
        Pipeline pipeline = new Pipeline(dto.getPipelineName(), dto.getPipelineDescription());
        pipeline.setRepoUrl(normalizeRepo(dto.getRepoUrl()));
        
        SSHKeyUtils.SSHKeyPair keyPair = SSHKeyUtils.generateRSAKeyPair();
        pipeline.setPublicKey(keyPair.getPublicKey());
        pipeline.setPrivateKey(keyPair.getPrivateKey());

        pipeline = pipelineRepository.save(pipeline);

        if (user.getPipelineList() == null) {
            user.setPipelineList(new ArrayList<>());
        }
        user.getPipelineList().add(pipeline);
        userRepository.save(user);
        return pipeline;
    }

    public Pipeline addBundle(AddBundleToPipeline dto) {
        Pipeline pipeline = pipelineRepository.findById(dto.getPipelineId()).orElseThrow();
        pipeline.setIpAddressBundle(dto.getBundleList());
        return pipelineRepository.save(pipeline);
    }

    public Pipeline addTasks(AddTasksToPipeline dto) {
        Pipeline pipeline = pipelineRepository.findById(dto.getPipelineId()).orElseThrow();
        pipeline.setTasksList(dto.getTaskList());
        return pipelineRepository.save(pipeline);
    }

    public Pipeline addSecrets(AddSecretsToPipeline dto) {
        Pipeline pipeline = pipelineRepository.findById(dto.getPipelineId()).orElseThrow();
        pipeline.setSecretList(dto.getSecretList());
        return pipelineRepository.save(pipeline);
    }

    public Pipeline setKeys(SetPublicPrivateKey dto) {
        Pipeline pipeline = pipelineRepository.findById(dto.getPipelineId()).orElseThrow();
        pipeline.setPublicKey(dto.getPublicKey());
        pipeline.setPrivateKey(dto.getPrivateKey());
        return pipelineRepository.save(pipeline);
    }

    @Async
    public void runPipelineAsync(Long id, String triggeredBy) {
        executePipeline(id, triggeredBy);
    }

    public String executePipeline(Long pipelineId, String triggeredBy) {
        Pipeline pipeline = transactionTemplate.execute(status -> {
            Pipeline p = pipelineRepository.findById(pipelineId).orElseThrow();
            if (p.getIpAddressBundle() != null) {
                p.getIpAddressBundle().size();
                for (Bundle b : p.getIpAddressBundle()) {
                    if (b.getIpAddresses() != null) b.getIpAddresses().size();
                }
            }
            if (p.getTasksList() != null) {
                p.getTasksList().size();
                for (Task t : p.getTasksList()) {
                    if (t.getCommandsList() != null) {
                        t.getCommandsList().size();
                        for (Commands c : t.getCommandsList()) {
                            if (c.getCommandList() != null) c.getCommandList().size();
                        }
                    }
                }
            }
            if (p.getSecretList() != null) p.getSecretList().size();
            return p;
        });

        int currentRun = historyRepository.findByPipelineIdOrderByTimestampDesc(pipelineId).size() + 1;

        PipelineHistory history = new PipelineHistory();
        history.setPipeline(pipeline);
        history.setRunNumber(currentRun);
        history.setTriggeredBy(triggeredBy);
        history.setStatus("RUNNING");
        history.setTimestamp(LocalDateTime.now());
        history = historyRepository.save(history);

        String failureMessage = null;
        StringBuilder finalLogsBuilder = new StringBuilder();

        try {
            if (pipeline.getIpAddressBundle() == null || pipeline.getIpAddressBundle().isEmpty()) {
                throw new RuntimeException("No bundles (server IPs) attached to this pipeline.");
            }
            if (pipeline.getTasksList() == null || pipeline.getTasksList().isEmpty()) {
                throw new RuntimeException("No tasks attached to this pipeline.");
            }
            
            String repoName = extractRepoName(pipeline.getRepoUrl());

            for (Bundle bundle : pipeline.getIpAddressBundle()) {
                for (String ip : bundle.getIpAddresses()) {
                    String secretExports = "";
                    if (pipeline.getSecretList() != null) {
                        for (Secret s : pipeline.getSecretList()) {
                            secretExports += "export " + s.getSecretName() +
                                    "=\"" + s.getSecretContent() + "\" && ";
                        }
                    }
                    
                    for (Task task : pipeline.getTasksList()) {
                        for (Commands cmd : task.getCommandsList()) {
                            List<String> rawCommands = cmd.getCommandList();
                            if (rawCommands == null || rawCommands.isEmpty()) continue;

                            StringBuilder scriptBuilder = new StringBuilder();
                            scriptBuilder.append("set -e\n");

                            for (String raw : rawCommands) {
                                String proc = raw.trim();
                                if (proc.isEmpty()) continue;

                                if (proc.startsWith("git clone") && repoName != null && proc.contains(repoName)) {
                                    scriptBuilder.append("rm -rf ").append(repoName).append(" || true\n");
                                }

                                if (proc.contains("apt") && (proc.contains("install") || proc.contains("update"))) {
                                    if (proc.startsWith("sudo ")) {
                                        proc = "sudo -n DEBIAN_FRONTEND=noninteractive " + proc.substring(5);
                                    } else {
                                        proc = "DEBIAN_FRONTEND=noninteractive " + proc;
                                    }
                                }
                                
                                scriptBuilder.append(substituteSecrets(proc, pipeline.getSecretList())).append("\n");
                            }

                            String outputText = executeSingleCommandStep(ip, bundle.getUsername(), pipeline.getPrivateKey(), 
                                    scriptBuilder.toString(), task.getTaskName());
                            finalLogsBuilder.append("--- Task: ").append(task.getTaskName()).append(" --- \n");
                            finalLogsBuilder.append(outputText).append("\n");
                        }
                    }
                }
            }
            history.setStatus("SUCCESS");
            history.setFinalLogs(finalLogsBuilder.toString());

        } catch (Exception e) {
            history.setStatus("FAILED");
            String[] splitMessage = e.getMessage().split(":", 2);
            if (splitMessage[0].equals("Task Failed")) {
               history.setFailedAtTask(splitMessage[1].trim());
            } else {
               history.setFailedAtTask("System Error: " + e.getMessage());
            }
            if (failureMessage == null) failureMessage = e.getMessage();
            System.err.println("Pipeline failed: " + failureMessage);
            finalLogsBuilder.append("\nFATAL ERROR: ").append(failureMessage);
            history.setFinalLogs(finalLogsBuilder.toString());
        }
        
        historyRepository.save(history);
        
        if ("SUCCESS".equals(history.getStatus())) {
            return "Pipeline execution finished successfully.";
        } else {
            return failureMessage;
        }
    }

    private String executeSingleCommandStep(String ip, String username, String privateKey, String command, String taskName) {
        String result = SSHExecutor.executeSingleCommand(ip, username, privateKey, command, liveOut -> {});
        boolean failed = result.toUpperCase().startsWith("ERROR:");
        if (failed) {
            throw new RuntimeException("Task Failed: " + taskName + "\n" + result);
        }
        return result;
    }

    public List<Pipeline> getAllPipeline(Long userId){
        User user = userRepository.findById(userId).orElseThrow(()->new RuntimeException("User not found"));
        return user.getPipelineList();
    }

    public String getPublicKey(Long id) {
        Pipeline pipeline = pipelineRepository.findById(id).orElseThrow(() -> new RuntimeException("Pipeline not found"));
        return pipeline.getPublicKey();
    }
}