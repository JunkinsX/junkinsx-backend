package com.example.jenkinsx.repository;

import com.example.jenkinsx.entity.Pipeline;
import com.example.jenkinsx.entity.Secret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecretRepository extends JpaRepository<Secret, Long> {
}