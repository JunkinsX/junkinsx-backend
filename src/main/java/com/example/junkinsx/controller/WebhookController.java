package com.example.junkinsx.controller;

import com.example.junkinsx.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
public class WebhookController {
    @Autowired
    private JobService jobService;
    @PostMapping("/{pipelineId}")
    public String trigger(@PathVariable Long pipelineId){
        jobService.createJob(pipelineId);
        return "Triggered";
    }
}
