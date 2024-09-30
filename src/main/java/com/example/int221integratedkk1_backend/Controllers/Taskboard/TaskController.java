package com.example.int221integratedkk1_backend.Controllers.Taskboard;

import com.example.int221integratedkk1_backend.DTOS.TaskDTO;
import com.example.int221integratedkk1_backend.DTOS.TaskRequest;
import com.example.int221integratedkk1_backend.Entities.Taskboard.TaskEntity;
import com.example.int221integratedkk1_backend.Services.Account.JwtTokenUtil;
import com.example.int221integratedkk1_backend.Services.Taskboard.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v3/boards/{boardId}/tasks")
@CrossOrigin(origins = {"http://localhost:5173", "http://ip23kk1.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th:8080", "http://ip23kk1.sit.kmutt.ac.th:8080"})
public class TaskController {

//    @Autowired
//    private TaskService taskService;
//
//    @Autowired
//    private JwtTokenUtil jwtTokenUtil;
//
//    @GetMapping("")
//    public ResponseEntity<List<TaskDTO>> getAllTasks(@PathVariable String boardId,
//                                                     @RequestParam(required = false) List<String> filterStatuses,
//                                                     @RequestParam(defaultValue = "status.name") String sortBy,
//                                                     @RequestParam(defaultValue = "asc") String sortDirection,
//                                                     @RequestHeader("Authorization") String requestTokenHeader) {
//        String userName = getUserNameFromToken(requestTokenHeader);
//        List<TaskDTO> tasks = taskService.getAllTasks(boardId, filterStatuses, sortBy, sortDirection, userName);
//        return ResponseEntity.ok(tasks);
//    }
//
//    @PostMapping("")
//    public ResponseEntity<TaskEntity> createTask(@PathVariable String boardId,
//                                                 @Valid @RequestBody TaskRequest taskRequest,
//                                                 @RequestHeader("Authorization") String requestTokenHeader) throws Throwable {
//        String userName = getUserNameFromToken(requestTokenHeader);
//        TaskEntity createdTask = taskService.createTask(boardId, taskRequest, userName);
//        return ResponseEntity.status(201).body(createdTask);
//    }
//
//    @GetMapping("/{taskId}")
//    public ResponseEntity<TaskEntity> getTaskById(@PathVariable String boardId,
//                                                  @PathVariable Integer taskId,
//                                                  @RequestHeader("Authorization") String requestTokenHeader) {
//        String userName = getUserNameFromToken(requestTokenHeader);
//        TaskEntity task = taskService.getTaskByIdAndBoard(taskId, boardId, userName);
//        return ResponseEntity.ok(task);
//    }
//
//    @PutMapping("/{taskId}")
//    public ResponseEntity<String> updateTask(@PathVariable String boardId,
//                                             @PathVariable Integer taskId,
//                                             @Valid @RequestBody TaskRequest taskRequest,
//                                             @RequestHeader("Authorization") String requestTokenHeader) throws Throwable {
//        String userName = getUserNameFromToken(requestTokenHeader);
//        boolean isUpdated = taskService.updateTask(taskId, boardId, taskRequest, userName);
//        return ResponseEntity.ok("Task updated successfully");
//    }
//
//    @DeleteMapping("/{taskId}")
//    public ResponseEntity<String> deleteTask(@PathVariable String boardId,
//                                             @PathVariable Integer taskId,
//                                             @RequestHeader("Authorization") String requestTokenHeader) {
//        String userName = getUserNameFromToken(requestTokenHeader);
//        boolean isDeleted = taskService.deleteTask(taskId, boardId, userName);
//        return ResponseEntity.ok("Task deleted successfully");
//    }
//
//    private String getUserNameFromToken(String requestTokenHeader) {
//        String token = requestTokenHeader.substring(7);
//        return jwtTokenUtil.getUsernameFromToken(token);
//    }
}
