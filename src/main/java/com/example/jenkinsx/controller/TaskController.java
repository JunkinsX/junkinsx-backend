package com.example.jenkinsx.controller;

import com.example.jenkinsx.dto.AddTask;
import com.example.jenkinsx.entity.Task;
import com.example.jenkinsx.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task")
@CrossOrigin("*")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/create")
    public Task create(@RequestBody AddTask dto) {
        return taskService.addTask(dto);
    }
    @GetMapping
    public List<Task> getTask(@RequestParam Long pipelineId){
        return taskService.getAllTasksOfUser(pipelineId);
    }
}