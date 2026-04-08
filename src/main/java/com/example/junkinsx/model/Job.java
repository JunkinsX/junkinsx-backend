package com.example.junkinsx.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@Entity
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long pipelineId;
    private String status;
    @Lob
    private String logs;
    private LocalDateTime createdAt;
}
