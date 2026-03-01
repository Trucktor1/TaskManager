package com.example.taskmanager.repository;

import com.example.taskmanager.model.Task;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Component("inMemoryTaskRepository")
@Primary
@Profile("dev")
public class InMemoryTaskRepository implements TaskRepository {
    private final List<Task> tasks = new CopyOnWriteArrayList<>();

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks);
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