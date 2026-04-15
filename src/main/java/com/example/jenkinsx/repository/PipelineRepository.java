package com.example.jenkinsx.repository;

import com.example.jenkinsx.entity.Pipeline;
import org.hibernate.boot.models.JpaAnnotations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PipelineRepository extends JpaRepository<Pipeline, Long> {
    List<Pipeline> findByRepoUrl(String repoUrl);
}
