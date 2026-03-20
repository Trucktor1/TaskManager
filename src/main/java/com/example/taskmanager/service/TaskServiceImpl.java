package com.example.taskmanager.service;

import com.example.taskmanager.annotation.Loggable;
import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.Task.Priority;
import com.example.taskmanager.model.Task.Status;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.config.AppProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final AppProperties appProperties;

    public TaskServiceImpl(TaskRepository taskRepository, AppProperties appProperties) {
        this.taskRepository = taskRepository;
        this.appProperties = appProperties;
    }

    @PostConstruct
    public void init() {
        System.out.println("TaskService с JPA инициализирован");
        System.out.println("База данных: H2 in-memory");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Завершение работы. Задач в БД: " + taskRepository.count());
    }

    private TaskResponse toResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setPriority(task.getPriority().toString());
        response.setStatus(task.getStatus().toString());
        return response;
    }

    private Task toEntity(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        Priority priority = request.getPriority();
        if (priority == null) {priority = appProperties.getDefaultPriority();}

        task.setPriority(priority);
        task.setStatus(Status.NEW);
        return task;
    }

    private void updateEntity(Task task, TaskRequest request) {
        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
    }
    @Override
    @Loggable
    public TaskResponse createTask(TaskRequest request) {
        if(request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Название задачи не может быть пустым");
        }

        validateTaskLimit();

        Task task = toEntity(request);
        Task savedTask = taskRepository.save(task);

        return toResponse(savedTask);
    }


    @Override
    @Transactional
    public Optional<TaskResponse> update(Long id, TaskRequest request) {
        return taskRepository.findById(id).map(task -> {
            updateEntity(task, request);
            return toResponse(taskRepository.save(task));
        });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if(!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
    }


    @Override
    @Loggable
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<TaskResponse> getAllTasksPaged(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return taskRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public Optional<TaskResponse> findById(Long id) {
        return taskRepository.findById(id).map(this::toResponse);
    }

    @Override
    public List<TaskResponse> getTasksByStatus(Status status) {
        return taskRepository.findByStatus(status).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getTasksByPriority(String priorityStr) {
        try {
            Priority priority = Priority.valueOf(priorityStr.toUpperCase());
            return taskRepository.findByPriority(priority).stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }

    @Override
    @Transactional
    public void updateTaskStatus(Long id, Status status){
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        task.setStatus(status);
    }


    @Override
    public List<TaskResponse> getAllWithFilters(String statusParam, String priorityParam) {
        if(statusParam != null && priorityParam != null) {
            try{
                Status status = Status.valueOf(statusParam.toUpperCase());
                Priority priority = Priority.valueOf(priorityParam.toUpperCase());
                return taskRepository.findByStatusAndPriority(status, priority)
                        .stream().map(this::toResponse).collect(Collectors.toList());
            }catch(IllegalArgumentException e){
                return List.of();
            }
        }else if(statusParam != null) {
            try{
                Status status = Status.valueOf(statusParam.toUpperCase());
                return taskRepository.findByStatus(status).stream()
                        .map(this::toResponse).collect(Collectors.toList());
            }catch(IllegalArgumentException e){
                return List.of();
            }
        }else if(priorityParam != null) {
            return getTasksByPriority(priorityParam);
        }

        return getAllTasks();
    }


    @Override
    public long getTaskCount() {
        return taskRepository.count();
    }

    @Override
    public void validateTaskLimit(){
        if (taskRepository.count() >= appProperties.getMaxTasks()){
            throw new IllegalStateException(
                    "Достигнут лимит задач: " + appProperties.getMaxTasks()
            );
        }
    }

    @Override
    public Map<String, Long> getStats() {
        List<Object[]> results = taskRepository.countTasksByStatus();

        Map<String, Long> stats = results.stream()
                .collect(Collectors.toMap(
                        result -> ((Status) result[0]).name(),
                        result -> ((Long) result[1])
                ));


        stats.put("TOTAL", taskRepository.count());
        return stats;
    }

    public List<TaskResponse> searchByKeyword(String keyword){
        if(keyword == null || keyword.trim().isEmpty()){
            return getAllTasks();
        }

        return taskRepository.searchByKeyword(keyword).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

}