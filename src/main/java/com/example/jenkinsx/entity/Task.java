package com.example.jenkinsx.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "task_id")
    private List<Commands> commandsList;

    public Task(String taskName, String taskDescription, List<Commands> commandsList) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.commandsList = commandsList;
    }
}