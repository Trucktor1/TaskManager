package com.example.taskmanager.dto;

import com.example.taskmanager.model.Task.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TaskRequest {
    @NotBlank(message = "Title cannot be blank")
    @Size(max = 255, message = "Title is too long (max 255 characters)")
    private String title;

    private String description;

    @NotNull(message = "Priority is required")
    private Priority priority;

    public TaskRequest(){

    }

    public TaskRequest(String title, String description, Priority priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public Priority getPriority() {return priority;}
    public void setPriority(Priority priority) {this.priority = priority;}
}
