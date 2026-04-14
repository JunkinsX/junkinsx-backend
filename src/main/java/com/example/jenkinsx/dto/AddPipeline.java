package com.example.jenkinsx.dto;

import lombok.Data;

@Data
public class AddPipeline {
    private String pipelineName;
    private String pipelineDescription;
    private Long userId;
}
