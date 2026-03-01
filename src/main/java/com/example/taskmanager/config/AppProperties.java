package com.example.taskmanager.config;

import com.example.taskmanager.model.Task;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {
    @Value("${app.name:Task Manager}")
    private String appName;

    @Value("${app.max-tasks:10}")
    private int maxTasks;

    @Value("${app.default-priority:LOW}")
    private String defaultPriority;

    @PostConstruct
    public void init() {
        System.out.println("Загружены настройки приложения");
        System.out.println("App Name: " + appName);
        System.out.println("Max Tasks: " + maxTasks);
        System.out.println("Default Priority: " + defaultPriority);
    }

    public String getAppName() { return appName; }
    public int getMaxTasks() { return maxTasks; }
    public Task.Priority getDefaultPriority() {
        return Task.Priority.valueOf(defaultPriority);
    }
}