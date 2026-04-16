package com.example.jenkinsx.service;

import com.example.jenkinsx.dto.*;
import com.example.jenkinsx.entity.*;
import com.example.jenkinsx.repository.PipelineLogRepository;
import com.example.jenkinsx.repository.PipelineRepository;
import com.example.jenkinsx.repository.UserRepository;
import com.example.jenkinsx.executor.SSHExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PipelineService {

    private final PipelineRepository pipelineRepository;
    private final UserRepository userRepository;
    private final PipelineLogRepository logRepository;

    public PipelineService(PipelineRepository pipelineRepository,
                           UserRepository userRepository, PipelineLogRepository pipelineLogRepository) {
        this.pipelineRepository = pipelineRepository;
        this.userRepository = userRepository;
        this.logRepository = pipelineLogRepository;
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

    public Pipeline addPipeline(AddPipeline dto) {

        User user = userRepository.findById(dto.getUserId()).orElseThrow();

        Pipeline pipeline = new Pipeline(
                dto.getPipelineName(),
                dto.getPipelineDescription()
        );

        pipeline.setRepoUrl(normalizeRepo(dto.getRepoUrl()));

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

    public String executePipeline(Long pipelineId) {
        Pipeline pipeline = pipelineRepository.findById(pipelineId).orElseThrow();
        pipeline.setStatus("RUNNING");
        pipelineRepository.save(pipeline);
        
        String failureMessage = null;

        try {
            if (pipeline.getIpAddressBundle() == null || pipeline.getTasksList() == null) {
                throw new RuntimeException("Pipeline incomplete: Missing bundles or tasks");
            }
            for (Bundle bundle : pipeline.getIpAddressBundle()) {
                for (String ip : bundle.getIpAddresses()) {
                    
                    // Layer 1: Environment Variables (Exports)
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
                            List<String> substitutedCommands = new ArrayList<>();
                            
                            // Layer 2: Literal Substitution
                            for (String raw : rawCommands) {
                                substitutedCommands.add(substituteSecrets(raw, pipeline.getSecretList()));
                            }

                            String finalScript = secretExports + String.join(" && ", substitutedCommands);

                            String result = SSHExecutor.executeSingleCommand(
                                    ip,
                                    bundle.getUsername(),
                                    pipeline.getPrivateKey(),
                                    finalScript
                            );

                            PipelineLog log = PipelineLog.builder()
                                    .pipelineId(pipeline.getId())
                                    .taskName(task.getTaskName())
                                    .command(String.join("; ", rawCommands))
                                    .output(result)
                                    .timestamp(LocalDateTime.now())
                                    .status(result.contains("ERROR") ? "FAILED" : "SUCCESS")
                                    .build();

                            logRepository.save(log);
                            
                            if (log.getStatus().equals("FAILED")) {
                                failureMessage = "Pipeline failed at task: [" + task.getTaskName() + "]";
                                throw new RuntimeException(failureMessage);
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
        }
        pipelineRepository.save(pipeline);
        
        if (pipeline.getStatus().equals("SUCCESS")) {
            return "Pipeline execution finished successfully.";
        } else {
            return failureMessage;
        }
    }
    @Async
    public void runPipelineAsync(Long id) {
        executePipeline(id);
    }
    public List<Pipeline> getAllPipeline(Long userId){
        User user = userRepository.findById(userId).orElseThrow(()->new RuntimeException("User not found"));
        return user.getPipelineList();
    }

    public String getPublicKey(Long pipelineId) {
        Pipeline pipeline = pipelineRepository.findById(pipelineId).orElseThrow(() -> new RuntimeException("Pipeline not found"));
        return pipeline.getPublicKey();
    }
}