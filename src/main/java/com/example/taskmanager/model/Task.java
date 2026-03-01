package com.example.taskmanager.model;

public class Task {
    private static Long counter = 0L;

    private Long id;
    private String title;
    private String description;
    private Priority priority;
    private Status status;

    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    public enum Status {
        NEW, IN_PROGRESS, DONE
    }

    public Task(String title, String description, Priority priority) {
        this.id = ++counter;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = Status.NEW;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Priority getPriority() { return priority; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("Task{id=%d, title='%s', priority=%s, status=%s}",
                id, title, priority, status);
    }
}