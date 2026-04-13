package com.example.jenkinsx.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Secret {
    @Id
    private Long id;
    private String SecretName;
    private String SecretContent;
}
