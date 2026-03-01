package com.example.taskmanager.repository;

import com.example.taskmanager.model.Task;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component("priorityTaskRepository")
@Profile("prod")
public class PriorityTaskRepository implements TaskRepository {
    private final List<Task> tasks = new CopyOnWriteArrayList<>();

    @Override
    public List<Task> findAll() {
        return tasks.stream()
                .sorted(Comparator.comparing((Task task) -> {
                    switch (task.getPriority()) {
                        case HIGH: return 0;
                        case MEDIUM: return 1;
                        case LOW: return 2;
                        default: return 3;
                    }
                }))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Task> findById(Long id) {
        return tasks.stream()
                .filter(task -> task.getId().equals(id))
                .findFirst();
    }

    @Override
    public Task save(Task task) {
        tasks.add(task);
        return task;
    }

    @Override
    public void delete(Long id) {
        tasks.removeIf(task -> task.getId().equals(id));
    }

    @Override
    public void updateStatus(Long id, Task.Status status) {
        findById(id).ifPresent(task -> task.setStatus(status));
    }
}