package com.example.jenkinsx.dto;

import com.example.jenkinsx.entity.Bundle;
import com.example.jenkinsx.entity.Task;
import lombok.Data;

import java.util.List;

@Data
public class TaskWithPipeline {
    private Long pipelineId;
    private String pipelineName;
    private String pipelineDescription;
    private List<Task> tasksList;
    public TaskWithPipeline(Long pipelineId, String pipelineName, String pipelineDescription, List<Task> TaskList) {
        this.pipelineId = pipelineId;
        this.pipelineName = pipelineName;
        this.pipelineDescription = pipelineDescription;
        this.tasksList = TaskList;
    }
}
