package com.example.junkinsx.controller;

import com.example.junkinsx.model.Job;
import com.example.junkinsx.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/logs")
public class LogsController {
    @Autowired
    private JobRepository repo;
    @PostMapping
    public void update(@RequestBody Map<String, String> body){
        Long jobId = Long.parseLong(body.get("jobId"));
        Job job = repo.findById(jobId).orElseThrow();
        job.setLogs(body.get("logs"));
        job.setStatus(body.get("status"));
        repo.save(job);
    }
}
