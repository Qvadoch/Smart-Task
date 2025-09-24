package org.example.task.repository;

import org.example.task.model.Task;
import org.example.task.model.TaskStatus;
import org.example.task.model.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserId(Long userId);

    List<Task> findByUserIdAndStatus(Long userId, TaskStatus status);

    List<Task> findByUserIdAndPriority(Long userId, Priority priority);

    List<Task> findByUserIdAndDeadlineBeforeAndStatusNot(Long userId, LocalDateTime date, TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:priority IS NULL OR t.priority = :priority)")
    List<Task> findByUserIdAndFilters(@Param("userId") Long userId,
                                      @Param("status") TaskStatus status,
                                      @Param("priority") Priority priority);

    boolean existsByIdAndUserId(Long id, Long userId);

    long countByUserIdAndStatus(Long userId, TaskStatus status);
}