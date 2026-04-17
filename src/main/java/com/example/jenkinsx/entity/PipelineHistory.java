package com.example.jenkinsx.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pipeline_history")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PipelineHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_id")
    @JsonBackReference
    private Pipeline pipeline;

    private Integer runNumber;
    private String triggeredBy; // "Manual Run" or "GitHub Webhook"
    private String status; // "RUNNING", "SUCCESS", "FAILED"
    private String failedAtTask;

    @Column(columnDefinition = "TEXT")
    private String finalLogs;

    private LocalDateTime timestamp;
}
