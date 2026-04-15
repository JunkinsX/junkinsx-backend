package com.example.jenkinsx.service;

import com.example.jenkinsx.dto.AddTask;
import com.example.jenkinsx.entity.Task;
import com.example.jenkinsx.repository.TaskRepository;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task addTask(AddTask dto) {

        Task task = new Task(
                dto.getTaskName(),
                dto.getTaskDescription(),
                dto.getCommandsList()
        );

        return taskRepository.save(task);
    }
}
