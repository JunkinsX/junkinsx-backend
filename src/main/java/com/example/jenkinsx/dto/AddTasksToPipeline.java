package com.example.jenkinsx.dto;

import com.example.jenkinsx.entity.Task;
import lombok.Data;

import java.util.List;

@Data
public class AddTasksToPipeline {
    private Long pipelineId;
    private List<Task> taskList;
}
