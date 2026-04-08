package com.example.junkinsx.controller;

import com.example.junkinsx.model.Job;
import com.example.junkinsx.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/job")
public class JobController {
    @Autowired
    private JobRepository repo;
    @GetMapping("/{id}")
    public Job get(@PathVariable Long id){
        return repo.findById(id).orElseThrow();
    }
}
