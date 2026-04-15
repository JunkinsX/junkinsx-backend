package com.example.jenkinsx.service;

import com.example.jenkinsx.dto.AddGetSecret;
import com.example.jenkinsx.entity.Secret;
import com.example.jenkinsx.repository.SecretRepository;
import org.springframework.stereotype.Service;

@Service
public class SecretService {

    private final SecretRepository secretRepository;

    public SecretService(SecretRepository secretRepository) {
        this.secretRepository = secretRepository;
    }

    public Secret addSecret(AddGetSecret dto) {

        Secret secret = new Secret(
                dto.getSecretName(),
                dto.getSecretContent()
        );

        return secretRepository.save(secret);
    }

    public Secret getSecret(Long id) {
        return secretRepository.findById(id).orElseThrow();
    }
}
