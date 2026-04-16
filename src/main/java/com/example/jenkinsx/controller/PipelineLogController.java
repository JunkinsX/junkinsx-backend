package com.example.jenkinsx.controller;

import com.example.jenkinsx.entity.PipelineLog;
import com.example.jenkinsx.repository.PipelineLogRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin("*")
public class PipelineLogController {

    private final PipelineLogRepository logRepository;

    public PipelineLogController(PipelineLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @GetMapping("/{pipelineId}")
    public List<PipelineLog> getLogs(@PathVariable Long pipelineId) {
        return logRepository.findByPipelineIdOrderByIdAsc(pipelineId);
    }
}