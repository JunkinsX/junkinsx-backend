package com.example.jenkinsx.service;

import com.example.jenkinsx.dto.*;
import com.example.jenkinsx.entity.*;
import com.example.jenkinsx.repository.PipelineLogRepository;
import com.example.jenkinsx.repository.PipelineRepository;
import com.example.jenkinsx.repository.UserRepository;
import com.example.jenkinsx.executor.SSHExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import com.example.jenkinsx.util.SSHKeyUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PipelineService {

    private final PipelineRepository pipelineRepository;
    private final UserRepository userRepository;
    private final PipelineLogRepository logRepository;
    private final TransactionTemplate transactionTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    public PipelineService(PipelineRepository pipelineRepository,
                           UserRepository userRepository, PipelineLogRepository pipelineLogRepository,
                           TransactionTemplate transactionTemplate, SimpMessagingTemplate messagingTemplate) {
        this.pipelineRepository = pipelineRepository;
        this.userRepository = userRepository;
        this.logRepository = pipelineLogRepository;
        this.transactionTemplate = transactionTemplate;
        this.messagingTemplate = messagingTemplate;
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

        Pipeline pipeline = new Pipeline(
                dto.getPipelineName(),
                dto.getPipelineDescription()
        );

        pipeline.setRepoUrl(normalizeRepo(dto.getRepoUrl()));
        
        // Auto-generate SSH Key Pair
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
    public void runPipelineAsync(Long id) {
        // Fetch a fresh instance inside the async thread to ensure a valid session
        executePipeline(id);
    }

    public String executePipeline(Long pipelineId) {
        Pipeline pipeline = transactionTemplate.execute(status -> {
            Pipeline p = pipelineRepository.findById(pipelineId).orElseThrow();
            
            // Eagerly load collections to avoid LazyInitializationException in the async thread
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

        pipeline.setStatus("RUNNING");
        pipelineRepository.save(pipeline);
        
        String failureMessage = null;

        try {
            // Automatically clear old logs before starting a new run
            clearLogs(pipelineId);
            
            if (pipeline.getIpAddressBundle() == null || pipeline.getIpAddressBundle().isEmpty()) {
                throw new RuntimeException("No bundles (server IPs) attached to this pipeline.");
            }
            if (pipeline.getTasksList() == null || pipeline.getTasksList().isEmpty()) {
                throw new RuntimeException("No tasks attached to this pipeline.");
            }
            
            String repoName = extractRepoName(pipeline.getRepoUrl());

            for (Bundle bundle : pipeline.getIpAddressBundle()) {
                for (String ip : bundle.getIpAddresses()) {
                    
                    // Add a connection-level log for debugging
                    PipelineLog connLog = PipelineLog.builder()
                            .pipelineId(pipeline.getId())
                            .taskName("Connection")
                            .command("ssh " + bundle.getUsername() + "@" + ip)
                            .output("Attempting to connect to " + ip + "...")
                            .status("RUNNING")
                            .timestamp(LocalDateTime.now())
                            .build();
                    connLog = logRepository.save(connLog);
                    messagingTemplate.convertAndSend("/topic/logs/" + pipeline.getId(), connLog);

                    // Layer 1: Environment Variables (Exports)
                    String secretExports = "";
                    if (pipeline.getSecretList() != null) {
                        for (Secret s : pipeline.getSecretList()) {
                            secretExports += "export " + s.getSecretName() +
                                    "=\"" + s.getSecretContent() + "\" && ";
                        }
                    }
                    
                    // Update connection log
                    connLog.setOutput("Connection to " + ip + " established. Starting tasks.");
                    connLog.setStatus("SUCCESS");
                    logRepository.save(connLog);
                    messagingTemplate.convertAndSend("/topic/logs/" + pipeline.getId(), connLog);

                    for (Task task : pipeline.getTasksList()) {
                        for (Commands cmd : task.getCommandsList()) {
                            
                            for (String raw : cmd.getCommandList()) {
                                String proc = raw.trim();
                                if (proc.isEmpty()) continue;

                                // Smart Cleanup: Handle git clone directory conflicts
                                if (proc.startsWith("git clone") && repoName != null && proc.contains(repoName)) {
                                    // We still want to handle this silently or as a separate step?
                                    // For "Real Raw" we should probably just execute what the user said
                                    // But git clone failing is a very common frustration. 
                                    // I will keep it but as an explicit separate raw command.
                                    String cleanup = "rm -rf " + repoName;
                                    executeSingleCommandStep(ip, bundle.getUsername(), pipeline.getPrivateKey(), 
                                            cleanup, "Cleanup", pipeline.getId(), secretExports, pipeline.getSecretList());
                                }

                                // Execute the user's raw command
                                executeSingleCommandStep(ip, bundle.getUsername(), pipeline.getPrivateKey(), 
                                        proc, task.getTaskName(), pipeline.getId(), secretExports, pipeline.getSecretList());
                            }
                        }
                    }
                }
            }
            pipeline.setStatus("SUCCESS");

        } catch (Exception e) {
            pipeline.setStatus("FAILED");
            if (failureMessage == null) failureMessage = e.getMessage();
            System.err.println("Pipeline failed: " + failureMessage);
            
            // Log the systemic failure so the frontend knows it crashed before/during tasks
            PipelineLog errLog = PipelineLog.builder()
                    .pipelineId(pipeline.getId())
                    .taskName("System Error")
                    .command("Internal Execution")
                    .output("Pipeline execution aborted: " + failureMessage)
                    .status("FAILED")
                    .timestamp(LocalDateTime.now())
                    .build();
            errLog = logRepository.save(errLog);
            messagingTemplate.convertAndSend("/topic/logs/" + pipelineId, errLog);
        }
        pipelineRepository.save(pipeline);
        
        if (pipeline.getStatus().equals("SUCCESS")) {
            return "Pipeline execution finished successfully.";
        } else {
            return failureMessage;
        }
    }

    private void executeSingleCommandStep(String ip, String username, String privateKey, String command, 
                                          String taskName, Long pipelineId, String secretExports, List<Secret> secrets) {
        
        String substituted = substituteSecrets(command, secrets);
        
        // Smart APT: Still inject DEBIAN_FRONTEND if using apt to prevent terminal hangs 
        // even in "Raw" mode, because a hung SSH session is never what the user wants.
        if (substituted.contains("apt") && (substituted.contains("install") || substituted.contains("update"))) {
            if (substituted.startsWith("sudo ")) {
                substituted = "sudo -n DEBIAN_FRONTEND=noninteractive " + substituted.substring(5);
            } else {
                substituted = "DEBIAN_FRONTEND=noninteractive " + substituted;
            }
        }

        String finalScript = secretExports + substituted;

        PipelineLog log = PipelineLog.builder()
                .pipelineId(pipelineId)
                .taskName(taskName)
                .command(substituted)
                .output("Executing...")
                .timestamp(LocalDateTime.now())
                .status("RUNNING")
                .build();

        final PipelineLog savedLog = logRepository.save(log);
        messagingTemplate.convertAndSend("/topic/logs/" + pipelineId, savedLog);

        String result = SSHExecutor.executeSingleCommand(
                ip,
                username,
                privateKey,
                finalScript,
                liveOut -> {
                    savedLog.setOutput(liveOut);
                    logRepository.save(savedLog);
                    messagingTemplate.convertAndSend("/topic/logs/" + pipelineId, savedLog);
                }
        );

        savedLog.setOutput(result);
        boolean failed = result.toUpperCase().startsWith("ERROR:");
        savedLog.setStatus(failed ? "FAILED" : "SUCCESS");
        logRepository.save(savedLog);
        messagingTemplate.convertAndSend("/topic/logs/" + pipelineId, savedLog);

        if (failed) {
            throw new RuntimeException("Pipeline failed at step: [" + command + "]");
        }
    }

    public List<Pipeline> getAllPipeline(Long userId){
        User user = userRepository.findById(userId).orElseThrow(()->new RuntimeException("User not found"));
        return user.getPipelineList();
    }

    public String getPublicKey(Long id) {
        Pipeline pipeline = pipelineRepository.findById(id).orElseThrow(() -> new RuntimeException("Pipeline not found"));
        return pipeline.getPublicKey();
    }

    public void clearLogs(Long pipelineId) {
        logRepository.deleteByPipelineId(pipelineId);
    }
}