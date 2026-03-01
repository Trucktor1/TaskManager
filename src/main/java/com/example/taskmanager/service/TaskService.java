package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import java.util.List;

public interface TaskService {
    Task createTask(String title, String description, Task.Priority priority);
    List<Task> getAllTasks();
    List<Task> getTasksByStatus(Task.Status status);
    List<Task> getTasksByPriority(Task.Priority priority);
    void updateTaskStatus(Long id, Task.Status status);
    long getTaskCount();
    void validateTaskLimit();
}