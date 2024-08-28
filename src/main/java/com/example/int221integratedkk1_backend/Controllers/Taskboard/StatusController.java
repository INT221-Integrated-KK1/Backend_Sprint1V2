package com.example.int221integratedkk1_backend.Controllers.Taskboard;

import com.example.int221integratedkk1_backend.Entities.Taskboard.StatusEntity;
import com.example.int221integratedkk1_backend.Exception.*;
import com.example.int221integratedkk1_backend.Services.Taskboard.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v2/statuses")
@CrossOrigin(origins = {"http://localhost:5173", "http://ip23kk1.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th:8080", "http://ip23kk1.sit.kmutt.ac.th:8080"})
public class StatusController {
    private final StatusService statusService;

    @Autowired
    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping
    public ResponseEntity<List<StatusEntity>> getAllStatuses() {
        List<StatusEntity> statuses = statusService.getAllStatuses();
        return ResponseEntity.ok(statuses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StatusEntity> getStatusById(@PathVariable int id) {
        try {
            StatusEntity status = statusService.getStatusById(id);
            return ResponseEntity.ok(status);
        } catch (ItemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping
    public ResponseEntity<Object> createStatus(@Valid @RequestBody StatusEntity statusEntity) {
        try {
            StatusEntity createdStatus = statusService.createStatus(statusEntity);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdStatus);
        } catch (ValidateInputException | DuplicateStatusException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateStatus(@PathVariable int id, @Valid @RequestBody StatusEntity updatedStatus) {
        try {
            String resultMessage = statusService.updateStatus(id, updatedStatus);
            return ResponseEntity.ok(resultMessage);
        } catch (ItemNotFoundException | ValidateInputException | DuplicateStatusException | UnManageStatusException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteStatus(@PathVariable int id) {
        try {
            statusService.deleteStatus(id);
            return ResponseEntity.ok("The status has been deleted");
        } catch (Throwable e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/{newId}")
    public ResponseEntity<Object> transferAndDeleteStatus(@PathVariable int id, @PathVariable int newId) {
        try {
            int transferredTasks = statusService.transferTasksAndDeleteStatus(id, newId);
            return ResponseEntity.ok(transferredTasks + " task(s) have been transferred and the status has been deleted");
        } catch (ItemNotFoundException | UnManageStatusException | InvalidTransferIdException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
