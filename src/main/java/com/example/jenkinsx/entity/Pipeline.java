package com.example.jenkinsx.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "pipelines")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pipeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pipelineName;
    private String pipelineDescription;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "pipeline_id")
    private List<Bundle> ipAddressBundle;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "pipeline_id")
    private List<Task> tasksList;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "pipeline_id")
    private List<Secret> secretList;

    private String publicKey;
    private String privateKey;

    @Column(length = 1000)
    private String repoUrl;
    private String webhookSecret;
    private String status;

    public Pipeline(String pipelineName, String pipelineDescription) {
        this.pipelineName = pipelineName;
        this.pipelineDescription = pipelineDescription;
    }
}