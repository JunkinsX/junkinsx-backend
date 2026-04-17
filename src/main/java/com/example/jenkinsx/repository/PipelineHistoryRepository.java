package com.example.jenkinsx.repository;

import com.example.jenkinsx.entity.PipelineHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PipelineHistoryRepository extends JpaRepository<PipelineHistory, Long> {
    List<PipelineHistory> findByPipelineIdOrderByTimestampDesc(Long pipelineId);
}
