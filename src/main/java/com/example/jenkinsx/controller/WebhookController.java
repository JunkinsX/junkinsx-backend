package com.example.jenkinsx.controller;

import com.example.jenkinsx.entity.Pipeline;
import com.example.jenkinsx.repository.PipelineRepository;
import com.example.jenkinsx.service.PipelineService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {

    private final PipelineRepository pipelineRepository;
    private final PipelineService pipelineService;

    public WebhookController(PipelineRepository pipelineRepository,
                             PipelineService pipelineService) {
        this.pipelineRepository = pipelineRepository;
        this.pipelineService = pipelineService;
    }

    @PostMapping("/github")
    public String githubWebhook(@RequestBody String payload,
                                @RequestHeader(value = "X-GitHub-Event", required = false) String event) {

        try {
            if (event == null || !event.equals("push")) {
                return "Ignored event: " + event;
            }
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(payload);
            String fullName = root.path("repository").path("full_name").asText();
            if (fullName == null || fullName.isEmpty()) {
                return "No repo full_name found";
            }
            
            System.out.println("Webhook Push for: " + fullName);
            
            List<Pipeline> allPipelines = pipelineRepository.findAll();
            int triggered = 0;
            for (Pipeline p : allPipelines) {
                if (p.getRepoUrl() != null && p.getRepoUrl().toLowerCase().contains(fullName.toLowerCase())) {
                    pipelineService.runPipelineAsync(p.getId(), "GitHub Webhook");
                    triggered++;
                }
            }
            return "Triggered pipelines: " + triggered;
        } catch (Exception e) {
            return "Webhook error: " + e.getMessage();
        }
    }

    private String normalizeRepo(String url) {
        if (url == null) return null;
        return url.replace(".git", "").trim();
    }
}