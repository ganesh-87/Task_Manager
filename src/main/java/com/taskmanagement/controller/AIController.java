package com.taskmanagement.controller;

import com.taskmanagement.model.Task;
import org.springframework.security.core.Authentication;
import com.taskmanagement.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/nlp-task")
@CrossOrigin(origins = "*") // allow frontend access
public class AIController {

    private final AIService geminiService;
    
    @Autowired
    private TaskService taskService;

    public AIController(AIService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping
    public ResponseEntity<Task> createTaskFromText(@RequestBody String request, Authentication authentication) {
        Task task = geminiService.generateTaskFromText(request);
        String username = authentication.getName(); // extract username from token/session
        Task createdTask = taskService.createTask(task, username);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }
}
