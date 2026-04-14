package com.example.jenkinsx.dto;

import com.example.jenkinsx.entity.Bundle;
import lombok.Data;

import java.util.List;

@Data
public class BundleWithPipeline {
    private Long pipelineId;
    private String pipelineName;
    private String pipelineDescription;
    private List<Bundle> bundleList;

    public BundleWithPipeline(Long pipelineId, String pipelineName, String pipelineDescription, List<Bundle> ipAddressBundle) {
        this.pipelineId = pipelineId;
        this.pipelineName = pipelineName;
        this.pipelineDescription = pipelineDescription;
        this.bundleList = ipAddressBundle;
    }
}
