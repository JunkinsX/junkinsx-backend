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

            // ✅ Only push events
            if (event == null || !event.equals("push")) {
                return "Ignored event: " + event;
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(payload);

            // ✅ Safe extraction
            String repoUrl = root.path("repository").path("clone_url").asText();
            String ref = root.path("ref").asText();

            if (repoUrl == null || repoUrl.isEmpty()) {
                return "No repo found";
            }

            repoUrl = normalizeRepo(repoUrl);

            String branch = ref.replace("refs/heads/", "");

            System.out.println("Repo: " + repoUrl);
            System.out.println("Branch: " + branch);

            List<Pipeline> pipelines = pipelineRepository.findByRepoUrl(repoUrl);

            for (Pipeline p : pipelines) {

                new Thread(() -> {
                    try {
                        pipelineService.executePipeline(p.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }

            return "Triggered pipelines: " + pipelines.size();

        } catch (Exception e) {
            return "Webhook error: " + e.getMessage();
        }
    }

    private String normalizeRepo(String url) {
        if (url == null) return null;
        return url.replace(".git", "").trim();
    }
}