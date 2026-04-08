package com.example.junkinsx.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Map;
@Getter
@Setter
@Entity
public class Pipeline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String repoUrl;
    private String serverIp;
    private String username;
    @Lob
    private String privateKey;
    @Lob
    private String publicKey;
    @OneToMany
    Map<String, Command> map;
    private String webhookSecret;
    private Long userId;
}
