package com.example.taskmanager.controller;

import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task> getAllTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority) {
        if (status == null && priority == null) {
            return taskService.getAllTasks();
        }
        return taskService.getAllWithFilters(status, priority);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskService.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        if (task == null) {
            return ResponseEntity.badRequest().build();
        }
        Task createdTask = taskService.createTask(
                task.getTitle(),
                task.getDescription(),
                task.getPriority()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        try{
            Task updatedTask = taskService.update(id, task);
            return ResponseEntity.ok(updatedTask);
        }catch(TaskNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String statusStr =  request.get("status");
            if(statusStr == null){
                return ResponseEntity.badRequest().build();
            }

            Task.Status status = Task.Status.valueOf(statusStr.toUpperCase());
            taskService.updateTaskStatus(id, status);
            return taskService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        try {
            taskService.delete(id);
            return ResponseEntity.noContent().build();
        }catch(TaskNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/stats")
    public Map<String, Long> getStats() {
        return taskService.getStats();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchTasks(@RequestParam String keyword) {
        List<Task> tasks = taskService.searchByKeyword(keyword);
        return ResponseEntity.ok(tasks);
    }
    
}
