package com.example.jenkinsx.dto;

import com.example.jenkinsx.entity.Bundle;
import com.example.jenkinsx.entity.Secret;
import lombok.Data;

import java.util.List;

@Data
public class SecretWithPipeline {
    private Long pipelineId;
    private String pipelineName;
    private String pipelineDescription;
    private List<Secret> secretList;
    public SecretWithPipeline(Long pipelineId, String pipelineName, String pipelineDescription, List<Secret> SecretList) {
        this.pipelineId = pipelineId;
        this.pipelineName = pipelineName;
        this.pipelineDescription = pipelineDescription;
        this.secretList = SecretList;
    }
}
