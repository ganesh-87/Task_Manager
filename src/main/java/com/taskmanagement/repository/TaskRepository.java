
package com.taskmanagement.repository;

import com.taskmanagement.model.Task;
import com.taskmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Find tasks by the user who created them
    List<Task> findByUser(User user);

    // Find a task by its ID
    Optional<Task> findByIdAndUser(Long id, User user);

    // Delete tasks by the user who created them
    void deleteById(Long id);
}
