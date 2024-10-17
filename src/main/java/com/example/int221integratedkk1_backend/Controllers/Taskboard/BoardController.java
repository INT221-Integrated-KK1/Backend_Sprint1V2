package com.example.int221integratedkk1_backend.Controllers.Taskboard;


import com.example.int221integratedkk1_backend.DTOS.BoardRequest;
import com.example.int221integratedkk1_backend.DTOS.BoardResponse;
import com.example.int221integratedkk1_backend.DTOS.TaskDTO;
import com.example.int221integratedkk1_backend.DTOS.TaskRequest;
import com.example.int221integratedkk1_backend.Entities.Account.UsersEntity;
import com.example.int221integratedkk1_backend.Entities.Account.Visibility;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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

    private static final Logger logger = LoggerFactory.getLogger(BoardController.class);



    @GetMapping("")
    public ResponseEntity<List<BoardResponse>> getAllBoards(@RequestHeader(value = "Authorization",required = false) String requestTokenHeader) {
        String ownerId = getUserIdFromToken(requestTokenHeader);
        List<BoardEntity> boards = boardService.getUserBoards(ownerId);
        List<BoardResponse> boardResponses = boards.stream().map(boardEntity -> {
            BoardResponse response = new BoardResponse();
            response.setId(boardEntity.getId());
            response.setName(boardEntity.getBoardName());
            UsersEntity owner = userService.findUserById(boardEntity.getOwnerId());
            BoardResponse.OwnerDTO ownerDTO = new BoardResponse.OwnerDTO(owner.getOid(), owner.getName());
            response.setOwner(ownerDTO);
            // Log the visibility of the board
            logger.info("In getAllBoards => Board ID: {}, Visibility: {}", boardEntity.getId(), boardEntity.getVisibility());
            return response;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(boardResponses);
    }

    @PostMapping("")
    public ResponseEntity<BoardResponse> createBoard(@RequestHeader(value = "Authorization",required = false) String requestTokenHeader,
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

        // Log the visibility of the created board
        logger.info("In createBoard => Created Board ID: {}, Visibility: {}", createdBoard.getId(), createdBoard.getVisibility());

        return ResponseEntity.status(201).body(boardResponse);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponse> getBoardById(@PathVariable String boardId,
                                                      @RequestHeader(value = "Authorization",required = false) String requestTokenHeader) {
//        String ownerId = getUserIdFromToken(requestTokenHeader);

        // Fetch the current board
        BoardEntity board = boardService.getBoardById(boardId);

        // Retrieve previous visibility from the board entity (assuming you have a method for that)
        String previousVisibility = board.getVisibility().name().toLowerCase(); // Get previous visibility here

        BoardResponse boardResponse = new BoardResponse();
        boardResponse.setId(board.getId());
        boardResponse.setName(board.getBoardName());

        // Get the current visibility
        String currentVisibility = board.getVisibility().name().toLowerCase();
        boardResponse.setVisibility(currentVisibility);

        // Log the visibility when fetching the board
        logger.info("Get Board by ID => Fetched Board ID: {}, Visibility: {}", board.getId(), currentVisibility);

        // Check if the visibility has changed
        if (!currentVisibility.equals(previousVisibility)) {
            logger.info("Visibility changed for Board ID: {}. Previous Visibility: {}, Current Visibility: {}",
                    board.getId(), previousVisibility, currentVisibility);
        }

        UsersEntity owner = userService.findUserById(board.getOwnerId());
        BoardResponse.OwnerDTO ownerDTO = new BoardResponse.OwnerDTO(owner.getOid(), owner.getName());
        boardResponse.setOwner(ownerDTO);

        return ResponseEntity.ok(boardResponse);
    }


    @PutMapping("/{boardId}")
    public ResponseEntity<String> updateBoard(@PathVariable String boardId,
                                              @RequestHeader(value = "Authorization",required = false) String requestTokenHeader,
                                              @Valid @RequestBody BoardEntity boardEntity) {
        String ownerId = getUserIdFromToken(requestTokenHeader);
        boardService.updateBoard(boardId, ownerId, boardEntity);
        // Log the visibility after updating the board
        logger.info("In Update Board Updated Board ID: {}, New Visibility: {}", boardId, boardEntity.getVisibility());
        return ResponseEntity.ok("Board updated successfully");
    }


    @DeleteMapping("/{boardId}")
    public ResponseEntity<String> deleteBoard(@PathVariable String boardId,
                                              @RequestHeader(value = "Authorization",required = false) String requestTokenHeader) {
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
                                                     @RequestHeader(value = "Authorization",required = false) String requestTokenHeader) {
//        String userName = getUserIdFromToken(requestTokenHeader);
        List<TaskDTO> tasks = taskService.getAllTasks(boardId, filterStatuses, sortBy, sortDirection, "");
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/{boardId}/tasks")
    public ResponseEntity<?> createTask(@PathVariable String boardId,
                                        @Valid @RequestBody(required = false) TaskRequest taskRequest,
                                        @RequestHeader(value = "Authorization",required = false) String requestTokenHeader) throws Throwable {
        if (taskRequest == null || taskRequest.getTitle() == null || taskRequest.getTitle().trim().isEmpty() || taskRequest.getStatus() == null) {
            return ResponseEntity.badRequest().body("Invalid task request body.");
        }


        String ownerId = getUserIdFromToken(requestTokenHeader);

        TaskEntity createdTask = taskService.createTask(boardId, taskRequest, ownerId);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }


    @GetMapping("/{boardId}/tasks/{taskId}")
    public ResponseEntity<TaskEntity> getTaskById(@PathVariable String boardId,
                                                  @PathVariable Integer taskId,
                                                  @RequestHeader(value = "Authorization",required = false) String requestTokenHeader) {
//        String userName = getUserIdFromToken(requestTokenHeader);
        TaskEntity task = taskService.getTaskByIdAndBoard(taskId, boardId, "");
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{boardId}/tasks/{taskId}")
    public ResponseEntity<?> updateTask(@PathVariable String boardId,
                                        @PathVariable Integer taskId,
                                        @Valid @RequestBody(required = false) TaskRequest taskRequest,
                                        @RequestHeader(value = "Authorization",required = false) String requestTokenHeader) throws Throwable {
        if (taskRequest == null || taskRequest.getTitle() == null || taskRequest.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid task update request body."); // 400 Bad Request
        }

//        String ownerId = getUserIdFromToken(requestTokenHeader);

        BoardEntity board = boardService.getBoardById(boardId);

        if (board == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Board not found"); // 404 Not Found
        }

        if (board.getVisibility() == Visibility.PRIVATE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to update tasks on this board."); // 403 Forbidden
        }

        Optional<TaskEntity> existingTask = taskService.findById(taskId);

        if (existingTask.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found."); // 404 Not Found
        }

        TaskEntity updatedTask = taskService.updateTask(taskId, boardId, taskRequest,"");
        return ResponseEntity.ok(updatedTask);
    }


    @DeleteMapping("/{boardId}/tasks/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable String boardId,
                                             @PathVariable Integer taskId,
                                             @RequestHeader(value = "Authorization",required = false) String requestTokenHeader) {
        String userName = getUserIdFromToken(requestTokenHeader);
        taskService.deleteTask(taskId, boardId, userName);
        return ResponseEntity.ok("Task deleted successfully");
    }

    // status
    @GetMapping("/{boardId}/statuses")
    public ResponseEntity<List<StatusEntity>> getAllStatuses(@PathVariable String boardId,
                                                             @RequestHeader(value = "Authorization",required = false) String requestTokenHeader) {
//        String userName = getUserIdFromToken(requestTokenHeader);
        List<StatusEntity> statuses = statusService.getStatusesByBoard(boardId, "");
        return ResponseEntity.ok(statuses);
    }

    @PostMapping("/{boardId}/statuses")
    public ResponseEntity<?> createStatus(@PathVariable String boardId,
                                          @RequestHeader(value = "Authorization",required = false) String requestTokenHeader,
                                          @Valid @RequestBody(required = false) StatusEntity statusEntity) {
        if (statusEntity == null || statusEntity.getName() == null || statusEntity.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid status request body."); // 400 Bad Request
        }

        String ownerId = getUserIdFromToken(requestTokenHeader);

        BoardEntity board = boardService.getBoardById(boardId);

        if (board == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Board not found"); // 404 Not Found
        }

        if (board.getVisibility() == Visibility.PRIVATE && !board.getOwnerId().equals(ownerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to add status to this board."); // 403 Forbidden
        }

        StatusEntity createdStatus = statusService.createStatus(boardId, ownerId, statusEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStatus);
    }


    @GetMapping("/{boardId}/statuses/{statusId}")
    public ResponseEntity<StatusEntity> getStatusById(@PathVariable String boardId,
                                                      @PathVariable Integer statusId,
                                                      @RequestHeader(value = "Authorization",required = false) String requestTokenHeader) {
//        String userName = getUserIdFromToken(requestTokenHeader);
        StatusEntity status = statusService.getStatusByIdAndBoard(statusId, boardId, "");
        return ResponseEntity.ok(status);
    }

    @PutMapping("/{boardId}/statuses/{statusId}")
    public ResponseEntity<?> updateStatus(@PathVariable String boardId,
                                          @PathVariable Integer statusId,
                                          @RequestHeader(value = "Authorization",required = false) String requestTokenHeader,
                                          @Valid @RequestBody(required = false) StatusEntity updatedStatus) {

        if (updatedStatus == null || updatedStatus.getName() == null || updatedStatus.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid status update request body."); // 400 Bad Request
        }

        String ownerId = getUserIdFromToken(requestTokenHeader);

        BoardEntity board = boardService.getBoardById(boardId);

        if (board == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Board not found"); // 404 Not Found
        }

        if (board.getVisibility() == Visibility.PRIVATE && !board.getOwnerId().equals(ownerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to update status on this board."); // 403 Forbidden
        }

        StatusEntity updatedEntity = statusService.updateStatus(statusId, boardId, ownerId, updatedStatus);

        return ResponseEntity.ok(updatedEntity);
    }


    @DeleteMapping("/{boardId}/statuses/{statusId}")
    public ResponseEntity<String> deleteStatus(@PathVariable String boardId,
                                               @PathVariable Integer statusId,
                                               @RequestHeader(value = "Authorization",required = false) String requestTokenHeader) {
        String userName = getUserIdFromToken(requestTokenHeader);
        statusService.deleteStatus(statusId, boardId, userName);
        return ResponseEntity.ok("Status deleted successfully");
    }

    @DeleteMapping("/{boardId}/statuses/{statusId}/{newId}")
    public ResponseEntity<String> deleteStatusAndReplace(@PathVariable String boardId,
                                                         @PathVariable Integer statusId,
                                                         @PathVariable Integer newId,
                                                         @RequestHeader(value = "Authorization",required = false) String requestTokenHeader) {
        String userName = getUserIdFromToken(requestTokenHeader);
        statusService.transferTasksAndDeleteStatus(statusId, newId, boardId, userName);
        return ResponseEntity.ok("Status replaced and deleted successfully");
    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<?> updateBoardVisibility(@PathVariable String boardId,
                                                   @RequestHeader(value = "Authorization",required = false) String requestTokenHeader,
                                                   @RequestBody(required = false) Map<String, String> body) {
        if (body == null || !body.containsKey("visibility")) {
            return ResponseEntity.badRequest().body("Missing 'visibility' field.");
        }

        String visibilityValue = body.get("visibility");
        if (!"public".equalsIgnoreCase(visibilityValue) && !"private".equalsIgnoreCase(visibilityValue)) {
            return ResponseEntity.badRequest().body("Invalid visibility value.");
        }


        String ownerId = getUserIdFromToken(requestTokenHeader);


        BoardEntity board = boardService.getBoardById(boardId);
        if (board == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Board not found"); // 404 Not Found
        }


        if (!board.getOwnerId().equals(ownerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to change visibility."); // 403 Forbidden
        }


        board.setVisibility(Visibility.valueOf(visibilityValue.toUpperCase()));
        boardService.updateBoardVisibility(board);


        BoardResponse boardResponse = new BoardResponse();
        boardResponse.setId(board.getId());
        boardResponse.setName(board.getBoardName());
        boardResponse.setVisibility(board.getVisibility().name().toLowerCase());

        UsersEntity owner = userService.findUserById(board.getOwnerId());
        BoardResponse.OwnerDTO ownerDTO = new BoardResponse.OwnerDTO(owner.getOid(), owner.getName());
        boardResponse.setOwner(ownerDTO);

        return ResponseEntity.ok(boardResponse);
    }


    private String getUserIdFromToken(String requestTokenHeader) {
        String token = requestTokenHeader.substring(7);
        return jwtTokenUtil.getUserIdFromToken(token);
    }


}
