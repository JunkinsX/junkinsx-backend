package com.example.jenkinsx.dto;

import com.example.jenkinsx.entity.Commands;
import lombok.Data;

import java.util.List;

@Data
public class AddTask {
    private String taskName;
    private String taskDescription;
    private List<Commands> commandsList;
}