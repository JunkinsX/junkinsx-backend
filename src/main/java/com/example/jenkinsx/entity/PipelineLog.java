package com.example.jenkinsx.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pipeline_logs")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PipelineLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pipelineId;

    @Column(columnDefinition = "TEXT")
    private String output;

    private String status; // SUCCESS / FAILED
}