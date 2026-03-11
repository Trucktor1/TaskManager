package com.example.taskmanager.service;

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

import java.time.LocalDateTime;
import java.util.HashMap;
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

    @Override
    @Transactional
    public Task createTask(String title, String description, Task.Priority priority) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Название задачи не может быть пустым!");
        }

        if (priority == null) {
            priority = appProperties.getDefaultPriority();
        }

        Task task = new Task(title, description, priority);
        Task savedTask = taskRepository.save(task);

        System.out.println("Задача сохранена в БД: " + savedTask);
        return savedTask;
    }


    @Override
    @Transactional
    public Task update(Long id, Task updatedTask) {
        Task existingTask = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));

        if (updatedTask.getTitle() != null && !updatedTask.getTitle().trim().isEmpty()) {
            existingTask.setTittle(updatedTask.getTitle());
        }
        if (updatedTask.getDescription() != null) {
            existingTask.setDescription(updatedTask.getDescription());
        }
        if (updatedTask.getPriority() != null) {
            existingTask.setPrority(updatedTask.getPriority());
        }

        return taskRepository.save(existingTask);
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
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Page<Task> getAllTasksPaged(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return taskRepository.findAll(pageable);
    }

    @Override
    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    @Override
    public List<Task> getTasksByStatus(Task.Status status) {
        return taskRepository.findByStatus(status);
    }

    @Override
    public List<Task> getTasksByPriority(Task.Priority priority) {
        return taskRepository.findByPriority(priority);
    }

    @Override
    @Transactional
    public void updateTaskStatus(Long id, Status status){
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        task.setStatus(status);
    }


    @Override
    public List<Task> getAllWithFilters(String statusParam, String priorityParam) {
        if(statusParam != null && priorityParam != null) {
            try{
                Status status = Status.valueOf(statusParam.toUpperCase());
                Priority priority = Priority.valueOf(priorityParam.toUpperCase());
                return taskRepository.findByStatusAndPriority(status, priority);
            }catch(IllegalArgumentException e){
                return List.of();
            }
        }else if(statusParam != null) {
            try{
                Status status = Status.valueOf(statusParam.toUpperCase());
                return taskRepository.findByStatus(status);
            }catch(IllegalArgumentException e){
                return List.of();
            }
        }else if(priorityParam != null) {
            try{
                Priority priority = Priority.valueOf(priorityParam.toUpperCase());
                return taskRepository.findByPriority(priority);
            }catch(IllegalArgumentException e){
                return List.of();
            }
        }

        return taskRepository.findAll();
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

        Map<String, Long> stats = new HashMap<>();
        for(Object[] result: results){
            Status status = (Status) result[0];
            Long count  = (Long) result[1];
            stats.put(status.name(), count);
        }

        stats.put("TOTAL", taskRepository.count());

        return stats;
    }

    public List<Task> searchByKeyword(String keyword){
        if(keyword == null || keyword.trim().isEmpty()){
            return taskRepository.findAll();
        }
        return taskRepository.searchByKeyword(keyword);
    }

    public Map<String, Long> getTodayStats() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        List<Task> todayTasks = taskRepository.findByCreatedAtAfter(startOfDay);

        return todayTasks.stream()
                .collect(Collectors.groupingBy(
                        task -> task.getStatus().name(),
                        Collectors.counting()
                ));
    }
}