package com.example.jenkinsx.repository;

import com.example.jenkinsx.entity.Bundle;
import com.example.jenkinsx.entity.Pipeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BundleRepository extends JpaRepository<Bundle, Long> {
}
