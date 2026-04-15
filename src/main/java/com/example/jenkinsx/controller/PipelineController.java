package com.example.jenkinsx.controller;

import com.example.jenkinsx.dto.*;
import com.example.jenkinsx.entity.Pipeline;
import com.example.jenkinsx.service.PipelineService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pipeline")
@CrossOrigin("*")
public class PipelineController {

    private final PipelineService pipelineService;

    public PipelineController(PipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    @PostMapping("/create")
    public Pipeline create(@RequestBody AddPipeline dto) {
        return pipelineService.addPipeline(dto);
    }

    @PostMapping("/bundle")
    public Pipeline addBundle(@RequestBody AddBundleToPipeline dto) {
        return pipelineService.addBundle(dto);
    }

    @PostMapping("/tasks")
    public Pipeline addTasks(@RequestBody AddTasksToPipeline dto) {
        return pipelineService.addTasks(dto);
    }

    @PostMapping("/secrets")
    public Pipeline addSecrets(@RequestBody AddSecretsToPipeline dto) {
        return pipelineService.addSecrets(dto);
    }

    @PostMapping("/keys")
    public Pipeline setKeys(@RequestBody SetPublicPrivateKey dto) {
        return pipelineService.setKeys(dto);
    }

    @PostMapping("/execute/{id}")
    public String execute(@PathVariable Long id) {
        return pipelineService.executePipeline(id);
    }
    @GetMapping()
    public List<Pipeline> GetAllPipeline(@RequestParam Long userId){
        return pipelineService.getAllPipeline(userId);
    }
}

