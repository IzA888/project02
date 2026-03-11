package com.example.project02.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.project02.model.AiTask;

@Repository
public interface TaskRepository extends JpaRepository<Long, AiTask> {

    AiTask save(AiTask task);
    
}
