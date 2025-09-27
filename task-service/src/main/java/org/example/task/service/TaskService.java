package org.example.task.service;

import lombok.extern.slf4j.Slf4j;
import org.example.task.model.Priority;
import org.example.task.model.Task;
import org.example.task.model.TaskStatus;
import org.example.task.repository.TaskRepository;
import org.example.task.dto.TaskRequest;
import org.example.task.dto.TaskResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CircuitBreakerFactory circuitBreakerFactory;

    public List<TaskResponse> getAllTasksByUser(Long userId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("taskService");
        return circuitBreaker.run(() -> {
            log.info("Getting all tasks for user: {}", userId);
            return taskRepository.findByUserId(userId)
                    .stream()
                    .map(TaskResponse::new)
                    .collect(Collectors.toList());
        }, throwable -> {
            log.error("Fallback for user {}: {}", userId, throwable.getMessage());
            return Collections.emptyList();
        });
    }

    public Optional<TaskResponse> getTaskById(Long id, Long userId) {
        log.info("Getting task by ID: {} for user: {}", id, userId);
        return taskRepository.findById(id)
                .filter(task -> task.getUserId().equals(userId))
                .map(TaskResponse::new);
    }

    public TaskResponse createTask(TaskRequest taskRequest) {
        log.info("Creating task for user: {}", taskRequest.getUserId());
        Task task = new Task(
                taskRequest.getTitle(),
                taskRequest.getDescription(),
                taskRequest.getPriority(),
                taskRequest.getDeadline(),
                taskRequest.getUserId()
        );

        if (taskRequest.getStatus() != null) {
            task.setStatus(taskRequest.getStatus());
        }

        Task savedTask = taskRepository.save(task);
        log.info("Task created with ID: {}", savedTask.getId());
        return new TaskResponse(savedTask);
    }

    public Optional<TaskResponse> updateTask(Long id, TaskRequest taskRequest) {
        log.info("Updating task ID: {} for user: {}", id, taskRequest.getUserId());
        return taskRepository.findById(id)
                .filter(task -> task.getUserId().equals(taskRequest.getUserId()))
                .map(task -> {
                    task.setTitle(taskRequest.getTitle());
                    task.setDescription(taskRequest.getDescription());
                    task.setStatus(taskRequest.getStatus());
                    task.setPriority(taskRequest.getPriority());
                    task.setDeadline(taskRequest.getDeadline());
                    return taskRepository.save(task);
                })
                .map(TaskResponse::new);
    }

    public Optional<TaskResponse> updateTaskStatus(Long id, Long userId, TaskStatus status) {
        log.info("Updating status for task ID: {} to: {}", id, status);
        return taskRepository.findById(id)
                .filter(task -> task.getUserId().equals(userId))
                .map(task -> {
                    task.setStatus(status);
                    return taskRepository.save(task);
                })
                .map(TaskResponse::new);
    }

    public boolean deleteTask(Long id, Long userId) {
        log.info("Deleting task ID: {} for user: {}", id, userId);
        Optional<Task> task = taskRepository.findById(id)
                .filter(t -> t.getUserId().equals(userId));

        if (task.isPresent()) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<TaskResponse> getTasksByStatus(Long userId, TaskStatus status) {
        log.info("Getting tasks by status: {} for user: {}", status, userId);
        return taskRepository.findByUserIdAndStatus(userId, status)
                .stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksByPriority(Long userId, Priority priority) {
        log.info("Getting tasks by priority: {} for user: {}", priority, userId);
        return taskRepository.findByUserIdAndPriority(userId, priority)
                .stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getOverdueTasks(Long userId) {
        log.info("Getting overdue tasks for user: {}", userId);
        return taskRepository.findByUserIdAndDeadlineBeforeAndStatusNot(
                        userId, LocalDateTime.now(), TaskStatus.DONE)
                .stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksWithFilters(Long userId, TaskStatus status, Priority priority) {
        log.info("Getting filtered tasks for user: {}, status: {}, priority: {}", userId, status, priority);
        return taskRepository.findByUserIdAndFilters(userId, status, priority)
                .stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }

    public long getTaskCountByStatus(Long userId, TaskStatus status) {
        log.info("Counting tasks with status: {} for user: {}", status, userId);
        return taskRepository.countByUserIdAndStatus(userId, status);
    }

    public boolean taskBelongsToUser(Long taskId, Long userId) {
        return taskRepository.existsByIdAndUserId(taskId, userId);
    }
}