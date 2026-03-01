package com.example.taskmanager.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@Scope("prototype")
public class TaskStatsService {
    private final String sessionId;

    public TaskStatsService() {
        this.sessionId = UUID.randomUUID().toString();
        System.out.println("Создан новый TaskStatsService с UUID: " + sessionId + " ===");
    }

    public String getSessionId() {
        return sessionId;
    }
}