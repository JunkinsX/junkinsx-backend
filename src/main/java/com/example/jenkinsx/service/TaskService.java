package com.example.jenkinsx.service;

import com.example.jenkinsx.dto.AddTask;
import com.example.jenkinsx.entity.Pipeline;
import com.example.jenkinsx.entity.Task;
import com.example.jenkinsx.entity.User;
import com.example.jenkinsx.repository.PipelineRepository;
import com.example.jenkinsx.repository.TaskRepository;
import com.example.jenkinsx.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final PipelineRepository pipelineRepository;

    public TaskService(TaskRepository taskRepository, PipelineRepository pipelineRepository) {
        this.taskRepository = taskRepository;
        this.pipelineRepository = pipelineRepository;
    }

    public Task addTask(AddTask dto) {

        Task task = new Task(
                dto.getTaskName(),
                dto.getTaskDescription(),
                dto.getCommandsList()
        );

        return taskRepository.save(task);
    }
    public List<Task> getAllTasksOfUser(Long pipelineId){
        Pipeline pipeline = pipelineRepository.findById(pipelineId).orElseThrow(()->new RuntimeException("Pipeline not found"));
        return pipeline.getTasksList();
    }
}
