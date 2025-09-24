package org.example.task.service;

import org.example.task.model.Task;
import org.example.task.model.TaskStatus;
import org.example.task.repository.TaskRepository;
import org.example.task.dto.TaskRequest;
import org.example.task.dto.TaskResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public List<TaskResponse> getAllTasksByUser(Long userId) {
        return taskRepository.findByUserId(userId)
                .stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }

    public Optional<TaskResponse> getTaskById(Long id, Long userId) {
        return taskRepository.findById(id)
                .filter(task -> task.getUserId().equals(userId))
                .map(TaskResponse::new);
    }

    public TaskResponse createTask(TaskRequest taskRequest) {
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
        return new TaskResponse(savedTask);
    }

    public Optional<TaskResponse> updateTask(Long id, TaskRequest taskRequest, Long userId) {
        return taskRepository.findById(id)
                .filter(task -> task.getUserId().equals(userId))
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

    public Optional<TaskResponse> updateTaskStatus(Long id, TaskStatus status, Long userId) {
        return taskRepository.findById(id)
                .filter(task -> task.getUserId().equals(userId))
                .map(task -> {
                    task.setStatus(status);
                    return taskRepository.save(task);
                })
                .map(TaskResponse::new);
    }

    public boolean deleteTask(Long id, Long userId) {
        Optional<Task> task = taskRepository.findById(id)
                .filter(t -> t.getUserId().equals(userId));

        if (task.isPresent()) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<TaskResponse> getTasksByStatus(Long userId, TaskStatus status) {
        return taskRepository.findByUserIdAndStatus(userId, status)
                .stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksByPriority(Long userId, org.example.task.model.Priority priority) {
        return taskRepository.findByUserIdAndPriority(userId, priority)
                .stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getOverdueTasks(Long userId) {
        return taskRepository.findByUserIdAndDeadlineBeforeAndStatusNot(
                        userId, LocalDateTime.now(), TaskStatus.DONE)
                .stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksWithFilters(Long userId, TaskStatus status, org.example.task.model.Priority priority) {
        return taskRepository.findByUserIdAndFilters(userId, status, priority)
                .stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }

    public long getTaskCountByStatus(Long userId, TaskStatus status) {
        return taskRepository.countByUserIdAndStatus(userId, status);
    }

    public boolean taskBelongsToUser(Long taskId, Long userId) {
        return taskRepository.existsByIdAndUserId(taskId, userId);
    }
}