package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TaskService {
    Task createTask(String title, String description, Task.Priority priority);
    List<Task> getAllTasks();
    Page<Task> getAllTasksPaged(int page, int size, String sortBy,  String sortDirection);
    Optional<Task> findById(Long id);
    Task update(Long id, Task task);
    void delete(Long id);

    List<Task> getTasksByStatus(Task.Status status);
    List<Task> getTasksByPriority(Task.Priority priority);
    List<Task> getAllWithFilters(String status, String priority);

    void updateTaskStatus(Long id, Task.Status status);

    long getTaskCount();
    Map<String, Long> getStats();
    List<Task> searchByKeyword(String keyword);
    Map<String, Long> getTodayStats();

    void validateTaskLimit();
}