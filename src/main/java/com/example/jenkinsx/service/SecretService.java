package com.example.jenkinsx.service;

import com.example.jenkinsx.dto.AddGetSecret;
import com.example.jenkinsx.entity.Secret;
import com.example.jenkinsx.repository.SecretRepository;

public class SecretService {
    private SecretRepository secretRepository;
    public SecretService(SecretRepository secretRepository){
        this.secretRepository = secretRepository;
    }
    public String AddSecret(AddGetSecret addSecret){
        Secret secret = new Secret(addSecret.getSecretName(), addSecret.getSecretContent());
        return "Secret created!";
    }
    public AddGetSecret GetSecret(Long secretId){
        Secret secret = secretRepository.findById(secretId).orElseThrow(()->new RuntimeException("Secret not found"));
        AddGetSecret addGetSecret = new AddGetSecret();
        addGetSecret.setSecretContent(secret.getSecretContent());
        addGetSecret.setSecretName(secret.getSecretName());
        return addGetSecret;
    }
}
