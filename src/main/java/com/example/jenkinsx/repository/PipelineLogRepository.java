package com.example.jenkinsx.repository;

import com.example.jenkinsx.entity.PipelineLog;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PipelineLogRepository extends JpaRepository<PipelineLog, Long> {

    List<PipelineLog> findByPipelineId(Long pipelineId);

    @Transactional
    void deleteByPipelineId(Long pipelineId);
}