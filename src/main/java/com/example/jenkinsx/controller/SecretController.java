package com.example.jenkinsx.controller;

import com.example.jenkinsx.dto.AddGetSecret;
import com.example.jenkinsx.entity.Secret;
import com.example.jenkinsx.service.SecretService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/secret")
@CrossOrigin("*")
public class SecretController {

    private final SecretService secretService;

    public SecretController(SecretService secretService) {
        this.secretService = secretService;
    }

    @PostMapping("/create")
    public Secret create(@RequestBody AddGetSecret dto) {
        return secretService.addSecret(dto);
    }

    @GetMapping("/{id}")
    public Secret get(@PathVariable Long id) {
        return secretService.getSecret(id);
    }
}