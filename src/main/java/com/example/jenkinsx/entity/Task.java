package com.example.jenkinsx.entity;

import jakarta.persistence.*;

import java.util.List;
import lombok.*;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String taskName;
    private String taskDescription;
    //list of commands to be executed via pipeline
    private List<Commands> commandsList;

    public Task(String taskName, String taskDescription, List<Commands> commandsList) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.commandsList = commandsList;
    }
}
