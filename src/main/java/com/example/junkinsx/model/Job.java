package com.example.junkinsx.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

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
