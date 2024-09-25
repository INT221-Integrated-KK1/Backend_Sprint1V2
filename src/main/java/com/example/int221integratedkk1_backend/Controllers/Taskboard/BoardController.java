package com.example.int221integratedkk1_backend.Controllers.Taskboard;


import com.example.int221integratedkk1_backend.DTOS.BoardRequest;
import com.example.int221integratedkk1_backend.DTOS.TaskDTO;
import com.example.int221integratedkk1_backend.DTOS.TaskRequest;
import com.example.int221integratedkk1_backend.Entities.Taskboard.BoardEntity;
import com.example.int221integratedkk1_backend.Entities.Taskboard.StatusEntity;
import com.example.int221integratedkk1_backend.Entities.Taskboard.TaskEntity;
import com.example.int221integratedkk1_backend.Services.Account.JwtTokenUtil;
import com.example.int221integratedkk1_backend.Services.Taskboard.BoardService;
import com.example.int221integratedkk1_backend.Services.Taskboard.StatusService;
import com.example.int221integratedkk1_backend.Services.Taskboard.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v3/boards")
@CrossOrigin(origins = {"http://localhost:5173", "http://ip23kk1.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th:8080", "http://ip23kk1.sit.kmutt.ac.th:8080"})
public class BoardController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private BoardService boardService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private StatusService statusService;

    // board
    @GetMapping("")
    public ResponseEntity<List<BoardEntity>> getAllBoards(@RequestHeader("Authorization") String requestTokenHeader) {
        String ownerId = getUserIdFromToken(requestTokenHeader);
        List<BoardEntity> boards = boardService.getUserBoards(ownerId);
        return ResponseEntity.ok(boards);
    }

    @PostMapping("")
    public ResponseEntity<BoardEntity> createBoard(@RequestHeader("Authorization") String requestTokenHeader,
                                                   @Valid @RequestBody BoardRequest boardRequest) {
        String ownerId = getUserIdFromToken(requestTokenHeader);
        BoardEntity createdBoard = boardService.createBoard(ownerId, boardRequest);
        return ResponseEntity.status(201).body(createdBoard);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardEntity> getBoardById(@PathVariable String boardId,
                                                    @RequestHeader("Authorization") String requestTokenHeader) {
        String ownerId = getUserIdFromToken(requestTokenHeader);
        BoardEntity board = boardService.getBoardByIdAndOwner(boardId, ownerId);
        return ResponseEntity.ok(board);
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<String> updateBoard(@PathVariable String boardId,
                                              @RequestHeader("Authorization") String requestTokenHeader,
                                              @Valid @RequestBody BoardEntity boardEntity) {
        String ownerId = getUserIdFromToken(requestTokenHeader);
        boardService.updateBoard(boardId, ownerId, boardEntity);
        return ResponseEntity.ok("Board updated successfully");
    }


    @DeleteMapping("/{boardId}")
    public ResponseEntity<String> deleteBoard(@PathVariable String boardId,
                                              @RequestHeader("Authorization") String requestTokenHeader) {
        String ownerId = getUserIdFromToken(requestTokenHeader);
        boardService.deleteBoard(boardId, ownerId);
        return ResponseEntity.ok("Board deleted successfully");
    }

    // task
    @GetMapping("/{boardId}/tasks")
    public ResponseEntity<List<TaskDTO>> getAllTasks(@PathVariable String boardId,
                                                     @RequestParam(required = false) List<String> filterStatuses,
                                                     @RequestParam(defaultValue = "status.name") String sortBy,
                                                     @RequestParam(defaultValue = "asc") String sortDirection,
                                                     @RequestHeader("Authorization") String requestTokenHeader) {
        String userName = getUserIdFromToken(requestTokenHeader);
        List<TaskDTO> tasks = taskService.getAllTasks(boardId, filterStatuses, sortBy, sortDirection, userName);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/{boardId}/tasks")
    public ResponseEntity<TaskEntity> createTask(@PathVariable String boardId,
                                                 @Valid @RequestBody TaskRequest taskRequest,
                                                 @RequestHeader("Authorization") String requestTokenHeader) throws Throwable {
        String userName = getUserIdFromToken(requestTokenHeader);
        TaskEntity createdTask = taskService.createTask(boardId, taskRequest, userName);
        return ResponseEntity.status(201).body(createdTask);
    }

    @GetMapping("/{boardId}/tasks/{taskId}")
    public ResponseEntity<TaskEntity> getTaskById(@PathVariable String boardId,
                                                  @PathVariable Integer taskId,
                                                  @RequestHeader("Authorization") String requestTokenHeader) {
        String userName = getUserIdFromToken(requestTokenHeader);
        TaskEntity task = taskService.getTaskByIdAndBoard(taskId, boardId, userName);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{boardId}/tasks/{taskId}")
    public ResponseEntity<String> updateTask(@PathVariable String boardId,
                                             @PathVariable Integer taskId,
                                             @Valid @RequestBody TaskRequest taskRequest,
                                             @RequestHeader("Authorization") String requestTokenHeader) throws Throwable {
        String userName = getUserIdFromToken(requestTokenHeader);
        taskService.updateTask(taskId, boardId, taskRequest, userName);
        return ResponseEntity.ok("Task updated successfully");
    }

    @DeleteMapping("/{boardId}/tasks/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable String boardId,
                                             @PathVariable Integer taskId,
                                             @RequestHeader("Authorization") String requestTokenHeader) {
        String userName = getUserIdFromToken(requestTokenHeader);
        taskService.deleteTask(taskId, boardId, userName);
        return ResponseEntity.ok("Task deleted successfully");
    }

    // status
    @GetMapping("/{boardId}/statuses")
    public ResponseEntity<List<StatusEntity>> getAllStatuses(@PathVariable String boardId,
                                                             @RequestHeader("Authorization") String requestTokenHeader) {
        String userName = getUserIdFromToken(requestTokenHeader);
        List<StatusEntity> statuses = statusService.getStatusesByBoard(boardId, userName);
        return ResponseEntity.ok(statuses);
    }

    @PostMapping("/{boardId}/statuses")
    public ResponseEntity<StatusEntity> createStatus(@PathVariable String boardId,
                                                     @RequestHeader("Authorization") String requestTokenHeader,
                                                     @Valid @RequestBody StatusEntity statusEntity) {
        String userName = getUserIdFromToken(requestTokenHeader);
        StatusEntity createdStatus = statusService.createStatus(boardId, userName, statusEntity);
        return ResponseEntity.status(201).body(createdStatus);
    }

    @GetMapping("/{boardId}/statuses/{statusId}")
    public ResponseEntity<StatusEntity> getStatusById(@PathVariable String boardId,
                                                      @PathVariable Integer statusId,
                                                      @RequestHeader("Authorization") String requestTokenHeader) {
        String userName = getUserIdFromToken(requestTokenHeader);
        StatusEntity status = statusService.getStatusByIdAndBoard(statusId, boardId, userName);
        return ResponseEntity.ok(status);
    }

    @PutMapping("/{boardId}/statuses/{statusId}")
    public ResponseEntity<String> updateStatus(@PathVariable String boardId,
                                               @PathVariable Integer statusId,
                                               @RequestHeader("Authorization") String requestTokenHeader,
                                               @Valid @RequestBody StatusEntity updatedStatus) {
        String userName = getUserIdFromToken(requestTokenHeader);
        String result = statusService.updateStatus(statusId, boardId, userName, updatedStatus);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{boardId}/statuses/{statusId}")
    public ResponseEntity<String> deleteStatus(@PathVariable String boardId,
                                               @PathVariable Integer statusId,
                                               @RequestHeader("Authorization") String requestTokenHeader) {
        String userName = getUserIdFromToken(requestTokenHeader);
        statusService.deleteStatus(statusId, boardId, userName);
        return ResponseEntity.ok("Status deleted successfully");
    }

    @DeleteMapping("/{boardId}/statuses/{statusId}/{newId}")
    public ResponseEntity<String> deleteStatusAndReplace(@PathVariable String boardId,
                                                         @PathVariable Integer statusId,
                                                         @PathVariable Integer newId,
                                                         @RequestHeader("Authorization") String requestTokenHeader) {
        String userName = getUserIdFromToken(requestTokenHeader);
        statusService.transferTasksAndDeleteStatus(statusId, newId, boardId, userName);
        return ResponseEntity.ok("Status replaced and deleted successfully");
    }

    private String getUserIdFromToken(String requestTokenHeader) {
        String token = requestTokenHeader.substring(7);
        return jwtTokenUtil.getUserIdFromToken(token);
    }
}
