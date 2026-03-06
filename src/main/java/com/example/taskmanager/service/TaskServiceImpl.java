package com.example.taskmanager.service;

import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.config.AppProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final AppProperties appProperties;
    private final ObjectProvider<TaskStatsService> statsServiceProvider;

    public TaskServiceImpl(TaskRepository taskRepository,
                           AppProperties appProperties,
                           ObjectProvider<TaskStatsService> statsServiceProvider) {
        this.taskRepository = taskRepository;
        this.appProperties = appProperties;
        this.statsServiceProvider = statsServiceProvider;
    }

    @PostConstruct
    public void init() {
        System.out.println("TaskService инициализирован");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Завершение работы. Задач в хранилище: " + getTaskCount() + " ===");
    }

    @Override
    public Task createTask(String title, String description, Task.Priority priority) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Название задачи не может быть пустым!");
        }

        validateTaskLimit();

        if (priority == null) {
            priority = appProperties.getDefaultPriority();
        }

        Task task = new Task(title, description, priority);
        return taskRepository.save(task);
    }

    @Override
    public Task update(Long id, Task updatedTask) {
        Task existingTask = findById(id).orElseThrow(() -> new TaskNotFoundException(id));

        Task task = new Task(
                updatedTask.getTitle(),
                updatedTask.getDescription(),
                updatedTask.getPriority());

        taskRepository.delete(id);
        return taskRepository.save(task);
    }

    @Override
    public void delete(Long id) {
        if (findById(id).isEmpty()) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.delete(id);
    }

    @Override
    public void validateTaskLimit() {
        if (getTaskCount() >= appProperties.getMaxTasks()) {
            throw new IllegalStateException("Достигнут лимит задач! Максимум: " + appProperties.getMaxTasks());
        }
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Optional<Task> findById(Long id) {
        return taskRepository.findAll().stream()
                .filter(task -> task.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Task> getTasksByStatus(Task.Status status) {
        return taskRepository.findAll().stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getTasksByPriority(Task.Priority priority) {
        return taskRepository.findAll().stream()
                .filter(task -> task.getPriority() == priority)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getAllWithFilters(String statusParam, String priorityParam) {
        return taskRepository.findAll().stream()
                .filter(task -> {
                    if (statusParam == null || statusParam.isEmpty()) return true;
                    try{
                        Task.Status status = Task.Status.valueOf(statusParam.toUpperCase());
                        return task.getStatus() == status;
                    }catch (IllegalArgumentException e){
                        return false;
                    }
                })
                .filter(task -> {
                    if (priorityParam == null || priorityParam.isEmpty()) return true;
                    try{
                        Task.Priority priority = Task.Priority.valueOf(priorityParam.toUpperCase());
                        return task.getPriority() == priority;
                    }catch (IllegalArgumentException e){
                        return false;
                    }
                }).collect(Collectors.toList());
    }

    @Override
    public void updateTaskStatus(Long id, Task.Status status) {
        taskRepository.updateStatus(id, status);
    }

    @Override
    public long getTaskCount() {
        return taskRepository.findAll().size();
    }

    @Override
    public Map<String, Long> getStats() {
        return taskRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        task -> task.getStatus().name(),
                        Collectors.counting()
                ));
    }

    public void showStats() {
        TaskStatsService statsService = statsServiceProvider.getObject();
        System.out.println("Статистика (UUID: " + statsService.getSessionId() + "): " +
                "Всего задач: " + getTaskCount());
    }
}