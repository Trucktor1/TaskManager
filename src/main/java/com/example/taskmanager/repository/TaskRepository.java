package com.example.taskmanager.repository;

import com.example.taskmanager.model.Task;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    List<Task> findAll();
    Optional<Task> findById(Long id);
    Task save(Task task);
    void delete(Long id);
    void updateStatus(Long id, Task.Status status);
}