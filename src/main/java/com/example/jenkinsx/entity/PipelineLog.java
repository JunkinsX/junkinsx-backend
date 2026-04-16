package com.example.jenkinsx.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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

    private String command;

    private String taskName;

    private LocalDateTime timestamp;

    private String status; // SUCCESS / FAILED
}