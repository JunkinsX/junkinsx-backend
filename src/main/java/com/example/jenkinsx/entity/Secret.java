package com.example.jenkinsx.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private Long id;
    private String SecretName;
    private String SecretContent;

    public Secret(String secretName, String secretContent) {
        this.SecretName = secretName;
        this.SecretContent = secretContent;
    }
}
