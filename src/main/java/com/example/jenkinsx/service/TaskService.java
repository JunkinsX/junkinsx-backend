package com.example.jenkinsx.service;

import com.example.jenkinsx.dto.AddTask;
import com.example.jenkinsx.entity.Task;
import com.example.jenkinsx.repository.TaskRepository;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    private TaskRepository taskRepository;
    public TaskService(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }
    public String addTask(AddTask addTask){
        Task task = new Task(addTask.getTaskName(), addTask.getTaskDescription(), addTask.getCommandsList());
        return "Task added, TaskId: " + task.getId() + ", TaskName: " + task.getTaskName() + ", TaskDescription: " + task.getTaskDescription();
    }
}
