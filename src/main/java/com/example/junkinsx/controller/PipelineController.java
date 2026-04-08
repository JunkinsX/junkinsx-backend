package com.example.junkinsx.controller;

import com.example.junkinsx.model.Pipeline;
import com.example.junkinsx.service.PipelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pipeline")
@CrossOrigin(origins = "*")
public class PipelineController {
    @Autowired
    private PipelineService service;
    @PostMapping
    public Pipeline create (@RequestBody Pipeline pipeline) throws Exception {
        return service.createPipeline(pipeline);
    }
    @GetMapping("/{id}")
    public Pipeline get(@PathVariable Long id){
        return service.getPipeline(id);
    }
    @GetMapping
    public List<Pipeline> getAll(){
        return service.getAll();
    }
}
