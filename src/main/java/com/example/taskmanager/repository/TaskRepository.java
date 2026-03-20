package com.example.taskmanager.repository;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.Task.Priority;
import com.example.taskmanager.model.Task.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task,Long> {
    List<Task> findByStatus(Status status);

    List<Task> findByPriority(Priority prority);

    List<Task> findByStatusAndPriority(Status status, Priority prority);

    List<Task> findByTitleContainingIgnoreCase(String keyword);

    long countByStatus(Status status);

    List<Task> findByCreatedAtAfter(LocalDateTime date);

    List<Task> findAllByOrderByCreatedAtDesc();

    Page<Task> findAll(Pageable pageable);
    Page<Task> findByStatus(Status status, Pageable pageable);
    Page<Task> findByPriority(Priority prority, Pageable pageable);

    @Query("SELECT t.status, COUNT(t) FROM Task t GROUP BY t.status")
    List<Object[]> countTasksByStatus();

    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Task> searchByKeyword(@Param("keyword") String keyword);

    @Query(value = "SELECT * FROM tasks WHERE priority = ?1", nativeQuery = true)
    List<Task> findByPriorityNative(String priority);

    @Query(value = "SELECT DATE(created_at) as day, COUNT(*) as count " +
            "FROM tasks GROUP BY DATE(created_at)", nativeQuery = true)
    List<Object[]> getDailyStatus();
}