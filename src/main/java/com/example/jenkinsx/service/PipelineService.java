package com.example.jenkinsx.service;

import com.example.jenkinsx.dto.*;
import com.example.jenkinsx.entity.*;
import com.example.jenkinsx.repository.PipelineLogRepository;
import com.example.jenkinsx.repository.PipelineRepository;
import com.example.jenkinsx.repository.UserRepository;
import com.example.jenkinsx.executor.SSHExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

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
        try {
            if (pipeline.getIpAddressBundle() == null || pipeline.getTasksList() == null) {
                throw new RuntimeException("Pipeline incomplete");
            }
            for (Bundle bundle : pipeline.getIpAddressBundle()) {
                for (String ip : bundle.getIpAddresses()) {
                    String secretExports = "";
                    if (pipeline.getSecretList() != null) {
                        for (Secret s : pipeline.getSecretList()) {
                            secretExports += "export " + s.getSecretName() +
                                    "=" + s.getSecretContent() + " && ";
                        }
                    }
                    for (Task task : pipeline.getTasksList()) {

                        for (Commands cmd : task.getCommandsList()) {

                            String script = secretExports +
                                    String.join(" && ", cmd.getCommandList());

                            String result = SSHExecutor.executeSingleCommand(
                                    ip,
                                    bundle.getUsername(),
                                    pipeline.getPrivateKey(),
                                    script
                            );

                            PipelineLog log = PipelineLog.builder()
                                    .pipelineId(pipeline.getId())
                                    .output(result)
                                    .status(result.contains("ERROR") ? "FAILED" : "SUCCESS")
                                    .build();

                            logRepository.save(log);
                        }
                    }
                }
            }
            pipeline.setStatus("SUCCESS");

        } catch (Exception e) {
            pipeline.setStatus("FAILED");
            System.out.println("Pipeline failed: " + e.getMessage());
        }
        pipelineRepository.save(pipeline);
        return "Pipeline execution finished";
    }
    @Async
    public void runPipelineAsync(Long id) {
        executePipeline(id);
    }
}