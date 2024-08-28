package com.example.int221integratedkk1_backend.Controllers.Taskboard;

import com.example.int221integratedkk1_backend.DTOS.TaskDTO;
import com.example.int221integratedkk1_backend.DTOS.TaskRequest;
import com.example.int221integratedkk1_backend.Entities.Taskboard.TaskEntity;
import com.example.int221integratedkk1_backend.Exception.ItemNotFoundException;
import com.example.int221integratedkk1_backend.Services.Taskboard.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v2/tasks")
@CrossOrigin(origins = {"http://localhost:5173", "http://ip23kk1.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th:8080", "http://ip23kk1.sit.kmutt.ac.th:8080"})
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("")
    public ResponseEntity<List<TaskDTO>> getAllTasks(@RequestParam(required = false) List<String> filterStatuses,
                                                     @RequestParam(defaultValue = "status.name") String sortBy,
                                                     @RequestParam(defaultValue = "asc") String sortDirection) {
        List<TaskDTO> taskDTOs = taskService.getAllTasks(filterStatuses, sortBy, sortDirection);
        return ResponseEntity.ok(taskDTOs);
    }

    @GetMapping("/{id}")
    public TaskEntity getTaskById(@PathVariable int id) {
        return taskService.getTaskById(id);
    }

    @PostMapping
    public ResponseEntity<TaskEntity> createTask(@Valid @RequestBody TaskRequest task) throws Throwable {
        TaskEntity createdTask = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateTask(@PathVariable int id, @Valid @RequestBody TaskRequest task) throws Throwable {
        boolean isUpdated = taskService.updateTask(id, task);
        if (isUpdated) {
            return ResponseEntity.ok("Task updated successfully");
        } else {
            throw new ItemNotFoundException("Task " + id + " does not exist !!!");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable int id) {
        boolean isDeleted = taskService.deleteTask(id);
        if (isDeleted) {
            return ResponseEntity.ok("Task deleted successfully");
        } else {
            throw new ItemNotFoundException("Task " + id + " does not exist !!!");
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
