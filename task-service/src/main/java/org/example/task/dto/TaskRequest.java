package org.example.task.dto;

import jakarta.validation.constraints.*;
import org.example.task.model.Priority;
import org.example.task.model.TaskStatus;

import java.time.LocalDateTime;

public class TaskRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private TaskStatus status = TaskStatus.TODO; // Значение по умолчанию

    private Priority priority = Priority.MEDIUM; // Значение по умолчанию

    @Future(message = "Deadline must be in the future")
    private LocalDateTime deadline;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be a positive number") // Новая аннотация
    private Long userId;

    // Геттеры и сеттеры
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}