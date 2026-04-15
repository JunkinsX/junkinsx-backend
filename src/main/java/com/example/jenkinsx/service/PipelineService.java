package com.example.jenkinsx.service;

import com.example.jenkinsx.dto.*;
import com.example.jenkinsx.entity.*;
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
        pipelineRepository.save(pipeline);
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
        return "";
    }
    public String SetPublicPrivateKey(Long pipelineId){
        Pipeline pipeline = pipelineRepository.findById(pipelineId).orElseThrow(()-> new RuntimeException("Pipeline not found"));
        //execute script or process to create a publickey, privatekey and save onto db
        return "";
    }
    public List<Bundle> getBundle(Long pipelineId){
        Pipeline pipeline = pipelineRepository.findById(pipelineId).orElseThrow(()-> new RuntimeException("Pipeline not found"));
        return pipeline.getIpAddressBundle();
    }
    public List<Task>  getTask(Long pipelineId){
        Pipeline pipeline = pipelineRepository.findById(pipelineId).orElseThrow(()-> new RuntimeException("Pipeline not found"));
        return pipeline.getTasksList();
    }
    public List<Secret> getSecret(Long pipelineId){
        Pipeline pipeline = pipelineRepository.findById(pipelineId).orElseThrow(()-> new RuntimeException("Pipeline not found"));
        return pipeline.getSecretList();
    }
    public String getPublicKey(Long pipelineId){
        Pipeline pipeline = pipelineRepository.findById(pipelineId).orElseThrow(()-> new RuntimeException("Pipeline not found"));
        return pipeline.getPublicKey();
    }

}

