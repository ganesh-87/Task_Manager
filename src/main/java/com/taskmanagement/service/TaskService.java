package com.taskmanagement.service;

import com.taskmanagement.model.Task;
import com.taskmanagement.model.User;
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    // Create a task and associate it with the user
    public Task createTask(Task task, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        task.setUser(user); // Associate the task with the logged-in user
        return taskRepository.save(task);
    }

    // Get all tasks for a specific user
    public List<Task> getTasksByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return taskRepository.findByUser(user); // Fetch tasks associated with the user
    }

    // Get task by ID, ensuring it belongs to the user
    public Optional<Task> getTaskById(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return taskRepository.findById(id)
                .filter(task -> task.getUser().getUsername().equals(username)); // Only fetch task if it belongs to the user
    }

    // Update task and ensure it belongs to the user
    public Task updateTask(Long id, Task updatedTask, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return taskRepository.findById(id)
                .map(task -> {
                    task.setTitle(updatedTask.getTitle());
                    task.setDescription(updatedTask.getDescription());
                    task.setStatus(updatedTask.getStatus());
                    task.setUser(user);
                    task.setDeadline(updatedTask.getDeadline());
                    return taskRepository.save(task);
                }).orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }

    // Delete task and ensure it belongs to the user
    public void deleteTask(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        taskRepository.deleteById(id); // Only delete if the task belongs to the user
    }
}
