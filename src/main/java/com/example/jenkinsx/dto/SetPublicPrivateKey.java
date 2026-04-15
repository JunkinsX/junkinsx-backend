package com.example.jenkinsx.dto;

import lombok.Data;

@Data
public class SetPublicPrivateKey {
    private Long pipelineId;
    private String publicKey;
    private String privateKey;
}