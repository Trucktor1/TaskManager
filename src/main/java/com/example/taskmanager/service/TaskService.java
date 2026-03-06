package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TaskService {
    Task createTask(String title, String description, Task.Priority priority);
    List<Task> getAllTasks();
    List<Task> getTasksByStatus(Task.Status status);
    List<Task> getTasksByPriority(Task.Priority priority);
    void updateTaskStatus(Long id, Task.Status status);
    long getTaskCount();
    void validateTaskLimit();

    Optional<Task> findById(Long id);
    Task update(Long id, Task task);
    void delete(Long id);
    List<Task> getAllWithFilters(String status, String priority);
    Map<String, Long> getStats();
}