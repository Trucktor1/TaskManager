package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.model.Task.Status;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TaskService {
    TaskResponse createTask(TaskRequest request);
    List<TaskResponse> getAllTasks();
    Page<TaskResponse> getAllTasksPaged(int page, int size, String sortBy, String direction);
    Optional<TaskResponse> findById(Long id);
    Optional<TaskResponse> update(Long id, TaskRequest request);
    void delete(Long id);

    List<TaskResponse> getTasksByStatus(Status status);
    List<TaskResponse> getTasksByPriority(String priority);
    List<TaskResponse> getAllWithFilters(String status, String priority);

    void updateTaskStatus(Long id, Status status);

    long getTaskCount();
    Map<String, Long> getStats();
    List<TaskResponse> searchByKeyword(String keyword);

    void validateTaskLimit();
}