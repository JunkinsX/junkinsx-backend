package com.example.jenkinsx.controller;

import com.example.jenkinsx.dto.AddBundleToUser;
import com.example.jenkinsx.dto.CreateBundle;
import com.example.jenkinsx.entity.Bundle;
import com.example.jenkinsx.service.BundleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bundle")
@CrossOrigin("*")
public class BundleController {

    private final BundleService bundleService;

    public BundleController(BundleService bundleService) {
        this.bundleService = bundleService;
    }

    @PostMapping("/create")
    public List<Bundle> create(@RequestBody CreateBundle dto) {
        return bundleService.createBundle(dto);
    }

    @PostMapping("/add-to-user")
    public List<Bundle> addToUser(@RequestBody AddBundleToUser dto) {
        return bundleService.addBundleToUser(dto);
    }
    @GetMapping
    public List<Bundle> GetBundle(@RequestParam Long userId){
        return bundleService.getBundle(userId);
    }
}
