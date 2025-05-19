package com.taskmanagement.controller;

import com.taskmanagement.model.Task;
import com.taskmanagement.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth/tasks")
@CrossOrigin(origins = "*") // allow frontend access
public class TaskController {

    @Autowired
    private TaskService taskService;

    // Create a new task for logged-in user
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task, Authentication authentication) {
        String username = authentication.getName(); // extract username from token/session
        Task createdTask = taskService.createTask(task, username);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    // Get all tasks of logged-in user
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(Authentication authentication) {
        String username = authentication.getName();
        List<Task> tasks = taskService.getTasksByUsername(username);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // Get task by ID if it belongs to the user
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        Optional<Task> task = taskService.getTaskById(id, username);
        return task.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                   .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Update a task if it belongs to the user
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task updatedTask, Authentication authentication) {
        String username = authentication.getName();
        try {
            Task updated = taskService.updateTask(id, updatedTask, username);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete a task if it belongs to the user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        try {
            taskService.deleteTask(id, username);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
