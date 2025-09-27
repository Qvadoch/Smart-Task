package org.example.task.fallback;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class FallbackController {

    @GetMapping("/fallback/task-service")
    public ResponseEntity<Map<String, Object>> taskServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Task service is temporarily unavailable");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(503).body(response);
    }
}
