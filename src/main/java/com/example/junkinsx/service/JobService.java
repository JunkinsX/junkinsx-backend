package com.example.junkinsx.service;

import com.example.junkinsx.model.Job;
import com.example.junkinsx.repository.JobRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class JobService {
    @Autowired
    private JobRepository repo;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    public void createJob(Long pipelineId){
        Job job = new Job();
        job.setPipelineId(pipelineId);
        job.setStatus("QUEUED");
        job.setCreatedAt(LocalDateTime.now());
        Job saved = repo.save(job);
        rabbitTemplate.convertAndSend("pipline_queue", saved.getId());
    }
}
