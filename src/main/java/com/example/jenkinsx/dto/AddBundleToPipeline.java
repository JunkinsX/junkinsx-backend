package com.example.jenkinsx.dto;

import com.example.jenkinsx.entity.Bundle;
import lombok.Data;

import java.util.List;

@Data
public class AddBundleToPipeline {
    private Long pipelineId;
    private List<Bundle> bundleList;
}
