package com.example.jenkinsx.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "secrets")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Secret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String secretName;
    private String secretContent;

    public Secret(String secretName, String secretContent) {
        this.secretName = secretName;
        this.secretContent = secretContent;
    }
}