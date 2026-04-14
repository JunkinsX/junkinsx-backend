package com.example.jenkinsx.repository;

import com.example.jenkinsx.entity.Pipeline;
import com.example.jenkinsx.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}