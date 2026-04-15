package com.example.jenkinsx.dto;

import lombok.Data;

@Data
public class PublicKeyWithPipeline {
    private Long pipelineId;
    private String publicKey;
    public PublicKeyWithPipeline(Long pipelineId, String publicKey){
        this.pipelineId = pipelineId;
        this.publicKey = publicKey;
    }
}
