package org.example.task.controller;

import org.example.task.dto.ApiResponse;
import org.example.task.dto.TaskRequest;
import org.example.task.dto.TaskResponse;
import org.example.task.model.TaskStatus;
import org.example.task.model.Priority;
import org.example.task.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getAllTasksByUser(
            @RequestHeader("X-User-Id") Long userId) {
        List<TaskResponse> tasks = taskService.getAllTasksByUser(userId);
        return ResponseEntity.ok(ApiResponse.success("Tasks retrieved successfully", tasks));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        return taskService.getTaskById(id, userId)
                .map(task -> ResponseEntity.ok(ApiResponse.success("Task retrieved successfully", task)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @Valid @RequestBody TaskRequest taskRequest,
            @RequestHeader("X-User-Id") Long userId) {

        // Убедимся, что userId в заголовке совпадает с userId в запросе
        if (!userId.equals(taskRequest.getUserId())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("User ID in header doesn't match request body"));
        }

        TaskResponse createdTask = taskService.createTask(taskRequest);
        return ResponseEntity.ok(ApiResponse.success("Task created successfully", createdTask));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest taskRequest,
            @RequestHeader("X-User-Id") Long userId) {

        return taskService.updateTask(id, taskRequest, userId)
                .map(task -> ResponseEntity.ok(ApiResponse.success("Task updated successfully", task)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTaskStatus(
            @PathVariable Long id,
            @RequestParam TaskStatus status,
            @RequestHeader("X-User-Id") Long userId) {

        return taskService.updateTaskStatus(id, status, userId)
                .map(task -> ResponseEntity.ok(ApiResponse.success("Task status updated successfully", task)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {

        if (taskService.deleteTask(id, userId)) {
            return ResponseEntity.ok(ApiResponse.success("Task deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/filter/status")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksByStatus(
            @RequestParam TaskStatus status,
            @RequestHeader("X-User-Id") Long userId) {

        List<TaskResponse> tasks = taskService.getTasksByStatus(userId, status);
        return ResponseEntity.ok(ApiResponse.success("Tasks filtered by status", tasks));
    }

    @GetMapping("/filter/priority")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksByPriority(
            @RequestParam Priority priority,
            @RequestHeader("X-User-Id") Long userId) {

        List<TaskResponse> tasks = taskService.getTasksByPriority(userId, priority);
        return ResponseEntity.ok(ApiResponse.success("Tasks filtered by priority", tasks));
    }

    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getOverdueTasks(
            @RequestHeader("X-User-Id") Long userId) {

        List<TaskResponse> tasks = taskService.getOverdueTasks(userId);
        return ResponseEntity.ok(ApiResponse.success("Overdue tasks retrieved", tasks));
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksWithFilters(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestHeader("X-User-Id") Long userId) {

        List<TaskResponse> tasks = taskService.getTasksWithFilters(userId, status, priority);
        return ResponseEntity.ok(ApiResponse.success("Tasks filtered successfully", tasks));
    }

    @GetMapping("/stats/count")
    public ResponseEntity<ApiResponse<Long>> getTaskCountByStatus(
            @RequestParam TaskStatus status,
            @RequestHeader("X-User-Id") Long userId) {

        long count = taskService.getTaskCountByStatus(userId, status);
        return ResponseEntity.ok(ApiResponse.success("Task count retrieved", count));
    }
}