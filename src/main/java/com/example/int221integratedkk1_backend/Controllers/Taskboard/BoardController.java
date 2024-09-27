package com.example.int221integratedkk1_backend.Controllers.Taskboard;


import com.example.int221integratedkk1_backend.DTOS.BoardRequest;
import com.example.int221integratedkk1_backend.DTOS.BoardResponse;
import com.example.int221integratedkk1_backend.DTOS.TaskDTO;
import com.example.int221integratedkk1_backend.DTOS.TaskRequest;
import com.example.int221integratedkk1_backend.Entities.Account.UsersEntity;
import com.example.int221integratedkk1_backend.Entities.Taskboard.BoardEntity;
import com.example.int221integratedkk1_backend.Entities.Taskboard.StatusEntity;
import com.example.int221integratedkk1_backend.Entities.Taskboard.TaskEntity;
import com.example.int221integratedkk1_backend.Exception.EmptyRequestBodyException;
import com.example.int221integratedkk1_backend.Services.Account.JwtTokenUtil;
import com.example.int221integratedkk1_backend.Services.Account.UserService;
import com.example.int221integratedkk1_backend.Services.Taskboard.BoardService;
import com.example.int221integratedkk1_backend.Services.Taskboard.StatusService;
import com.example.int221integratedkk1_backend.Services.Taskboard.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<List<BoardResponse>> getAllBoards(@RequestHeader("Authorization") String requestTokenHeader) {
        String ownerId = getUserIdFromToken(requestTokenHeader);
        List<BoardEntity> boards = boardService.getUserBoards(ownerId);
        List<BoardResponse> boardResponses = boards.stream().map(boardEntity -> {
            BoardResponse response = new BoardResponse();
            response.setId(boardEntity.getId());
            response.setName(boardEntity.getBoardName());
            UsersEntity owner = userService.findUserById(boardEntity.getOwnerId());
            BoardResponse.OwnerDTO ownerDTO = new BoardResponse.OwnerDTO(owner.getOid(), owner.getName());
            response.setOwner(ownerDTO);

            return response;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(boardResponses);
    }

    @PostMapping("")
    public ResponseEntity<BoardResponse> createBoard(@RequestHeader("Authorization") String requestTokenHeader,
                                                     @Valid @RequestBody(required = false) BoardRequest boardRequest) {
        if (boardRequest == null || boardRequest.getName() == null || boardRequest.getName().trim().isEmpty()) {
            throw new EmptyRequestBodyException("Request body is missing or board name is empty.");
        }

        String ownerId = getUserIdFromToken(requestTokenHeader);
        BoardEntity createdBoard = boardService.createBoard(ownerId, boardRequest);

        BoardResponse boardResponse = new BoardResponse();
        boardResponse.setId(createdBoard.getId());
        boardResponse.setName(createdBoard.getBoardName());

        UsersEntity owner = userService.findUserById(createdBoard.getOwnerId());
        BoardResponse.OwnerDTO ownerDTO = new BoardResponse.OwnerDTO(owner.getOid(), owner.getName());
        boardResponse.setOwner(ownerDTO);

        return ResponseEntity.status(201).body(boardResponse);
    }






    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponse> getBoardById(@PathVariable String boardId,
                                                      @RequestHeader("Authorization") String requestTokenHeader) {
        String ownerId = getUserIdFromToken(requestTokenHeader);
        BoardEntity board = boardService.getBoardByIdAndOwner(boardId, ownerId);

        BoardResponse boardResponse = new BoardResponse();
        boardResponse.setId(board.getId());
        boardResponse.setName(board.getBoardName());
        UsersEntity owner = userService.findUserById(board.getOwnerId());
        BoardResponse.OwnerDTO ownerDTO = new BoardResponse.OwnerDTO(owner.getOid(), owner.getName());
        boardResponse.setOwner(ownerDTO);

        return ResponseEntity.ok(boardResponse);
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
public ResponseEntity<TaskEntity> updateTask(@PathVariable String boardId,
                                             @PathVariable Integer taskId,
                                             @Valid @RequestBody TaskRequest taskRequest,
                                             @RequestHeader("Authorization") String requestTokenHeader) throws Throwable {
    String userName = getUserIdFromToken(requestTokenHeader);
    TaskEntity updatedTask = taskService.updateTask(taskId, boardId, taskRequest, userName);
    return ResponseEntity.ok(updatedTask);
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
public ResponseEntity<StatusEntity> updateStatus(@PathVariable String boardId,
                                                 @PathVariable Integer statusId,
                                                 @RequestHeader("Authorization") String requestTokenHeader,
                                                 @Valid @RequestBody StatusEntity updatedStatus) {

    String userName = getUserIdFromToken(requestTokenHeader);
    StatusEntity updatedEntity = statusService.updateStatus(statusId, boardId, userName, updatedStatus);

    return ResponseEntity.ok(updatedEntity);
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
