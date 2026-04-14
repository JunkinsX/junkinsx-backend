package com.example.jenkinsx.service;

import com.example.jenkinsx.dto.*;
import com.example.jenkinsx.entity.Bundle;
import com.example.jenkinsx.entity.Pipeline;
import com.example.jenkinsx.entity.User;
import com.example.jenkinsx.repository.PipelineRepository;
import com.example.jenkinsx.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.nio.channels.Pipe;
import java.util.List;

@Service
public class PipelineService {
    private PipelineRepository pipelineRepository;
    private UserRepository userRepository;
    public PipelineService(PipelineRepository pipelineRepository, UserRepository userRepository){
        this.pipelineRepository = pipelineRepository;
        this.userRepository = userRepository;
    }
    public Pipeline addPipeline(AddPipeline addPipeline) {
        Pipeline pipeline = new Pipeline(addPipeline.getPipelineName(), addPipeline.getPipelineDescription());
        User user = userRepository.findById(addPipeline.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
        List<Pipeline> list = user.getPipelineList();
        list.add(pipeline);
        return pipeline;
    }
    public BundleWithPipeline addBundleToPipeline(AddBundleToPipeline bundleToPipeline) {
        Pipeline pipeline = pipelineRepository.findById(bundleToPipeline.getPipelineId()).orElseThrow(() -> new RuntimeException("Pipeline not found"));
        pipeline.setIpAddressBundle(bundleToPipeline.getBundleList());
        return new BundleWithPipeline(pipeline.getId(), pipeline.getPipelineName(), pipeline.getPipelineDescription(), pipeline.getIpAddressBundle());
    }
    public TaskWithPipeline addTasksToPipeline(AddTasksToPipeline addTasksToPipeline){
        Pipeline pipeline = pipelineRepository.findById(addTasksToPipeline.getPipelineId()).orElseThrow(()->new RuntimeException("Pipeline not found"));
        pipeline.setTasksList(addTasksToPipeline.getTaskList());
        return new TaskWithPipeline(pipeline.getId(), pipeline.getPipelineName(), pipeline.getPipelineDescription(), pipeline.getTasksList());
    }
    public SecretWithPipeline addSecretsToPipeline(AddSecretsToPipeline addSecretsToPipeline){
        Pipeline pipeline = pipelineRepository.findById(addSecretsToPipeline.getPipelineId()).orElseThrow(()->new RuntimeException("Pipeline not found"));
        pipeline.setSecretList(addSecretsToPipeline.getSecretList());
        return new SecretWithPipeline(pipeline.getId(), pipeline.getPipelineName(), pipeline.getPipelineDescription(), pipeline.getSecretList());
    }
    public String ExecutePipeline(Long pipelineId){
        Pipeline pipeline = pipelineRepository.findById(pipelineId).orElseThrow(()-> new RuntimeException("Pipeline not found"));
        //execute or push onto rabbitmq/any message queue
    }

}
