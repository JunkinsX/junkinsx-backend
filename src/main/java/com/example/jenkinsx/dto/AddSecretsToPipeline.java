package com.example.jenkinsx.dto;

import com.example.jenkinsx.entity.Secret;
import lombok.Data;

import java.util.List;

@Data
public class AddSecretsToPipeline {
    private Long pipelineId;
    private List<Secret> secretList;
}