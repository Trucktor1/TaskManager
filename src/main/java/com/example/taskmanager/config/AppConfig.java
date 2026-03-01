package com.example.taskmanager.config;

import com.example.taskmanager.repository.PriorityTaskRepository;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class AppConfig {

    @Bean
    @Profile("prod")
    public String priorityRepositoryLogger(@Qualifier("priorityTaskRepository")
                                           TaskRepository priorityRepository) {
        String message = "Явное получение PriorityTaskRepository через @Qualifier. " +
                "Класс: " + priorityRepository.getClass().getSimpleName();
        System.out.println(message);
        return message;
    }

    @Bean
    @Profile("dev")
    public String inMemoryRepositoryLogger(@Qualifier("inMemoryTaskRepository")
                                           TaskRepository inMemoryRepository) {
        String message = "Явное получение InMemoryTaskRepository через @Qualifier. " +
                "Класс: " + inMemoryRepository.getClass().getSimpleName();
        System.out.println(message);
        return message;
    }
}