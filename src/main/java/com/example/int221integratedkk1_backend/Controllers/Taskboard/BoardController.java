package com.example.int221integratedkk1_backend.Controllers.Taskboard;

import com.example.int221integratedkk1_backend.DTOS.*;
import com.example.int221integratedkk1_backend.Entities.Taskboard.AccessRight;
import com.example.int221integratedkk1_backend.Entities.Taskboard.Collaborator;
import com.example.int221integratedkk1_backend.Entities.Account.UsersEntity;
import com.example.int221integratedkk1_backend.Entities.Account.Visibility;
import com.example.int221integratedkk1_backend.Entities.Taskboard.BoardEntity;
import com.example.int221integratedkk1_backend.Entities.Taskboard.StatusEntity;
import com.example.int221integratedkk1_backend.Entities.Taskboard.TaskEntity;
import com.example.int221integratedkk1_backend.Exception.EmptyRequestBodyException;
import com.example.int221integratedkk1_backend.Exception.UnauthorizedException;
import com.example.int221integratedkk1_backend.Services.Taskboard.CollabService;
import com.example.int221integratedkk1_backend.Services.Account.JwtTokenUtil;
import com.example.int221integratedkk1_backend.Services.Account.UserService;
import com.example.int221integratedkk1_backend.Services.Taskboard.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    private CollabService collabService;

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(BoardController.class);

    // Get all boards (including collaboration boards)
//    @GetMapping("")
//    public ResponseEntity<List<BoardResponse>> getAllBoards(@RequestHeader(value = "Authorization", required = false) String requestTokenHeader) {
//        String ownerId = getUserIdFromToken(requestTokenHeader);
//        List<BoardEntity> boards = boardService.getUserBoards(ownerId);
//        List<BoardResponse> boardResponses = boards.stream().map(boardEntity -> {
//            BoardResponse response = new BoardResponse();
//            response.setId(boardEntity.getId());
//            response.setName(boardEntity.getBoardName());
//            response.setVisibility(boardEntity.getVisibility().name().toLowerCase());
//            UsersEntity owner = userService.findUserById(boardEntity.getOwnerId());
//            BoardResponse.OwnerDTO ownerDTO = new BoardResponse.OwnerDTO(owner.getOid(), owner.getName());
//            response.setOwner(ownerDTO);
//            List<CollabDTO> collaborators = collabService.getCollaborators(boardEntity.getId());
//            response.setCollaborators(collaborators);
//            logger.info("Board ID: {}, Visibility: {}", boardEntity.getId(), boardEntity.getVisibility());
//            return response;
//        }).collect(Collectors.toList());
//
//        return ResponseEntity.ok(boardResponses);
//    }
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllBoards(@RequestHeader(value = "Authorization", required = false) String requestTokenHeader) {

        String ownerId = getUserIdFromToken(requestTokenHeader);

        List<BoardEntity> personalBoards = boardService.getUserBoards(ownerId);

        List<BoardEntity> collabBoards = collabService.getBoardsWhereUserIsCollaborator(ownerId);

        List<BoardResponse> personalBoardResponses = personalBoards.stream().map(boardEntity -> {
            BoardResponse response = new BoardResponse();
            response.setId(boardEntity.getId());
            response.setName(boardEntity.getBoardName());
            response.setVisibility(boardEntity.getVisibility().name().toLowerCase());
            UsersEntity owner = userService.findUserById(boardEntity.getOwnerId());
            BoardResponse.OwnerDTO ownerDTO = new BoardResponse.OwnerDTO(owner.getOid(), owner.getName());
            response.setOwner(ownerDTO);

            List<CollabDTO> collaborators = collabService.getCollaborators(boardEntity.getId());
            response.setCollaborators(collaborators);

            logger.info("Board ID: {}, Visibility: {}", boardEntity.getId(), boardEntity.getVisibility());
            return response;
        }).collect(Collectors.toList());

        List<BoardResponse> collabBoardResponses = collabBoards.stream().map(boardEntity -> {
            BoardResponse response = new BoardResponse();
            response.setId(boardEntity.getId());
            response.setName(boardEntity.getBoardName());
            response.setVisibility(boardEntity.getVisibility().name().toLowerCase());
            UsersEntity owner = userService.findUserById(boardEntity.getOwnerId());
            BoardResponse.OwnerDTO ownerDTO = new BoardResponse.OwnerDTO(owner.getOid(), owner.getName());
            response.setOwner(ownerDTO);

            List<CollabDTO> collaborators = collabService.getCollaborators(boardEntity.getId());
            response.setCollaborators(collaborators);

            logger.info("Collab Board ID: {}, Visibility: {}", boardEntity.getId(), boardEntity.getVisibility());
            return response;
        }).collect(Collectors.toList());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("personalBoards", personalBoardResponses);
        responseBody.put("collabBoards", collabBoardResponses);

        return ResponseEntity.ok(responseBody);
    }


    // Create a new board
    @PostMapping("")
    public ResponseEntity<BoardResponse> createBoard(@RequestHeader(value = "Authorization", required = false) String requestTokenHeader,
                                                     @Valid @RequestBody(required = false) BoardRequest boardRequest) {
        if (boardRequest == null || boardRequest.getName() == null || boardRequest.getName().trim().isEmpty()) {
            throw new EmptyRequestBodyException("Request body is missing or board name is empty.");
        }

        String ownerId = getUserIdFromToken(requestTokenHeader);
        BoardEntity createdBoard = boardService.createBoard(ownerId, boardRequest);

        BoardResponse boardResponse = new BoardResponse();
        boardResponse.setId(createdBoard.getId());
        boardResponse.setName(createdBoard.getBoardName());
        boardResponse.setVisibility(createdBoard.getVisibility().name().toLowerCase());
        UsersEntity owner = userService.findUserById(createdBoard.getOwnerId());
        BoardResponse.OwnerDTO ownerDTO = new BoardResponse.OwnerDTO(owner.getOid(), owner.getName());
        boardResponse.setOwner(ownerDTO);

        logger.info("Created Board ID: {}, Visibility: {}", createdBoard.getId(), createdBoard.getVisibility());
        return ResponseEntity.status(HttpStatus.CREATED).body(boardResponse);
    }

    // Get a specific board by ID
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponse> getBoardById(@PathVariable String boardId,
                                                      @RequestHeader(value = "Authorization", required = false) String requestTokenHeader) {
        BoardEntity board = boardService.getBoardById(boardId);
        String previousVisibility = board.getVisibility().name().toLowerCase();

        BoardResponse boardResponse = new BoardResponse();
        boardResponse.setId(board.getId());
        boardResponse.setName(board.getBoardName());
        boardResponse.setVisibility(board.getVisibility().name().toLowerCase());

        logger.info("Fetched Board ID: {}, Visibility: {}", board.getId(), board.getVisibility());

        if (!board.getVisibility().name().toLowerCase().equals(previousVisibility)) {
            logger.info("Visibility changed for Board ID: {}. Previous Visibility: {}, Current Visibility: {}",
                    board.getId(), previousVisibility, board.getVisibility().name().toLowerCase());
        }

        UsersEntity owner = userService.findUserById(board.getOwnerId());
        BoardResponse.OwnerDTO ownerDTO = new BoardResponse.OwnerDTO(owner.getOid(), owner.getName());
        boardResponse.setOwner(ownerDTO);

        return ResponseEntity.ok(boardResponse);
    }

    // Update board visibility
    @PatchMapping("/{boardId}")
    public ResponseEntity<?> updateBoardVisibility(@PathVariable String boardId,
                                                   @RequestHeader(value = "Authorization", required = false) String requestTokenHeader,
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
        if (!board.getOwnerId().equals(ownerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to change visibility.");
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

    // Task Management
    @GetMapping("/{boardId}/tasks")
    public ResponseEntity<List<TaskDTO>> getAllTasks(@PathVariable String boardId,
                                                     @RequestParam(required = false) List<String> filterStatuses,
                                                     @RequestParam(defaultValue = "status.name") String sortBy,
                                                     @RequestParam(defaultValue = "asc") String sortDirection,
                                                     @RequestHeader(value = "Authorization", required = false) String requestTokenHeader) {
        List<TaskDTO> tasks = taskService.getAllTasks(boardId, filterStatuses, sortBy, sortDirection, "");
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/{boardId}/tasks")
    public ResponseEntity<?> createTask(@PathVariable String boardId,
                                        @Valid @RequestBody(required = false) TaskRequest taskRequest,
                                        @RequestHeader(value = "Authorization", required = false) String requestTokenHeader) throws Throwable {
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
                                                  @RequestHeader(value = "Authorization", required = false) String requestTokenHeader) {
        TaskEntity task = taskService.getTaskByIdAndBoard(taskId, boardId, "");
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{boardId}/tasks/{taskId}")
    public ResponseEntity<?> updateTask(@PathVariable String boardId,
                                        @PathVariable Integer taskId,
                                        @Valid @RequestBody(required = false) TaskRequest taskRequest,
                                        @RequestHeader(value = "Authorization", required = false) String requestTokenHeader) throws Throwable {
        if (taskRequest == null || taskRequest.getTitle() == null || taskRequest.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid task update request body.");
        }

        BoardEntity board = boardService.getBoardById(boardId);
        if (board.getVisibility() == Visibility.PRIVATE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to update tasks on this board.");
        }

        Optional<TaskEntity> existingTask = taskService.findById(taskId);
        if (existingTask.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found.");
        }

        TaskEntity updatedTask = taskService.updateTask(taskId, boardId, taskRequest, "");
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{boardId}/tasks/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable String boardId,
                                             @PathVariable Integer taskId,
                                             @RequestHeader(value = "Authorization", required = false) String requestTokenHeader) {
        String userName = getUserIdFromToken(requestTokenHeader);
        taskService.deleteTask(taskId, boardId, userName);
        return ResponseEntity.ok("Task deleted successfully");
    }

    // Status Management

    @GetMapping("/{boardId}/statuses")
    public ResponseEntity<List<StatusEntity>> getAllStatuses(@PathVariable String boardId,
                                                             @RequestHeader(value = "Authorization", required = false) String requestTokenHeader) {
        List<StatusEntity> statuses = statusService.getStatusesByBoard(boardId, "");
        return ResponseEntity.ok(statuses);
    }

    @PostMapping("/{boardId}/statuses")
    public ResponseEntity<?> createStatus(@PathVariable String boardId,
                                          @RequestHeader(value = "Authorization", required = false) String requestTokenHeader,
                                          @Valid @RequestBody(required = false) StatusEntity statusEntity) {
        if (statusEntity == null || statusEntity.getName() == null || statusEntity.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid status request body.");
        }

        String ownerId = getUserIdFromToken(requestTokenHeader);
        BoardEntity board = boardService.getBoardById(boardId);

        if (board.getVisibility() == Visibility.PRIVATE && !board.getOwnerId().equals(ownerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to add status to this board.");
        }

        StatusEntity createdStatus = statusService.createStatus(boardId, ownerId, statusEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStatus);
    }

    @GetMapping("/{boardId}/statuses/{statusId}")
    public ResponseEntity<StatusEntity> getStatusById(@PathVariable String boardId,
                                                      @PathVariable Integer statusId,
                                                      @RequestHeader(value = "Authorization", required = false) String requestTokenHeader) {
        StatusEntity status = statusService.getStatusByIdAndBoard(statusId, boardId, "");
        return ResponseEntity.ok(status);
    }

    @PutMapping("/{boardId}/statuses/{statusId}")
    public ResponseEntity<?> updateStatus(@PathVariable String boardId,
                                          @PathVariable Integer statusId,
                                          @RequestHeader(value = "Authorization", required = false) String requestTokenHeader,
                                          @Valid @RequestBody(required = false) StatusEntity updatedStatus) {
        if (updatedStatus == null || updatedStatus.getName() == null || updatedStatus.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid status update request body.");
        }

        String ownerId = getUserIdFromToken(requestTokenHeader);
        BoardEntity board = boardService.getBoardById(boardId);

        if (board.getVisibility() == Visibility.PRIVATE && !board.getOwnerId().equals(ownerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to update status on this board.");
        }

        StatusEntity updatedEntity = statusService.updateStatus(statusId, boardId, ownerId, updatedStatus);
        return ResponseEntity.ok(updatedEntity);
    }

    @DeleteMapping("/{boardId}/statuses/{statusId}")
    public ResponseEntity<String> deleteStatus(@PathVariable String boardId,
                                               @PathVariable Integer statusId,
                                               @RequestHeader(value = "Authorization", required = false) String requestTokenHeader) {
        String userName = getUserIdFromToken(requestTokenHeader);
        statusService.deleteStatus(statusId, boardId, userName);
        return ResponseEntity.ok("Status deleted successfully");
    }


//    @GetMapping("/{boardId}/collabs")
//    public ResponseEntity<List<CollabDTO>> getCollaborators(@PathVariable String boardId,
//                                                            @RequestHeader("Authorization") String requestTokenHeader) {
//        // Get the user ID from the token
//        String userId = getUserIdFromToken(requestTokenHeader);
//
//        // Fetch the board entity by its ID
//        BoardEntity board = boardService.getBoardById(boardId);
//
//        // Check if the current user is the owner of the board
//        if (board.getOwnerId().equals(userId)) {
//            // The user is the owner, so allow access to the collaborator list
//            List<CollabDTO> collaborators = collabService.getCollaborators(boardId);
//            return ResponseEntity.ok(collaborators);
//        }
//
//        // Check if the user is a collaborator on this board with READ/WRITE access
//        Optional<Collaborator> collaborator = collabService.getCollaboratorByBoardIdAndCollabId(boardId, userId);
//        if (collaborator.isPresent() &&
//                (collaborator.get().getAccessLevel() == AccessRight.READ || collaborator.get().getAccessLevel() == AccessRight.WRITE)) {
//            // The user is a collaborator with READ or WRITE access, so allow access to the list
//            List<CollabDTO> collaborators = collabService.getCollaborators(boardId);
//            return ResponseEntity.ok(collaborators);
//        }
//
//        // If the user is neither the owner nor a collaborator with appropriate access, return a 403 Forbidden
//        throw new UnauthorizedException("You are not authorized to view collaborators of this board");
//    }

//    @GetMapping("/{boardId}/collabs")
//    public ResponseEntity<List<CollabDTO>> getCollaborators(@PathVariable String boardId,
//                                                            @RequestHeader("Authorization") String requestTokenHeader) {
//        String userId = getUserIdFromToken(requestTokenHeader);
//
//        BoardEntity board = boardService.getBoardById(boardId);
//
//        if (!isOwnerOrCollaborator(board, userId)) {
//            logger.warn("User {} is not authorized to view collaborators for board {}", userId, boardId);
//            throw new UnauthorizedException("You are not authorized to view collaborators of this board");
//        }
//
//        List<CollabDTO> collaborators = collabService.getCollaborators(boardId);
//
//        return ResponseEntity.ok(collaborators);
//    }
@GetMapping("/{boardId}/collabs")
public ResponseEntity<List<CollabDTO>> getCollaborators(@PathVariable String boardId,
                                                        @RequestHeader("Authorization") String requestTokenHeader) {
    String userId = getUserIdFromToken(requestTokenHeader);

    BoardEntity board = boardService.getBoardById(boardId);

    // Allow if the user is either the owner or a collaborator with READ or WRITE access
    if (!board.getOwnerId().equals(userId) && !collabService.isCollaborator(boardId, userId)) {
        throw new UnauthorizedException("You are not authorized to view collaborators of this board");
    }

    List<CollabDTO> collaborators = collabService.getCollaborators(boardId);

    return ResponseEntity.ok(collaborators);
}



    @GetMapping("/{boardId}/collabs/{collabId}")
    public ResponseEntity<CollabDTO> getCollaboratorById(@PathVariable String boardId,
                                                         @PathVariable String collabId,
                                                         @RequestHeader("Authorization") String requestTokenHeader) {
        String userId = getUserIdFromToken(requestTokenHeader);
        BoardEntity board = boardService.getBoardById(boardId);


        boolean isAuthorized = board.getVisibility() == Visibility.PUBLIC ||
                board.getOwnerId().equals(userId) ||
                collabService.isCollaborator(boardId, userId);

        if (!isAuthorized) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Optional<Collaborator> collaborator = collabService.getCollaboratorByBoardIdAndCollabId(boardId, collabId);

        if (collaborator.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Collaborator collab = collaborator.get();
        CollabDTO collabDTO = new CollabDTO();
        collabDTO.setOid(collab.getCollabsId());
        collabDTO.setName(collab.getCollabsName());
        collabDTO.setEmail(collab.getCollabsEmail());
        collabDTO.setAccessRight(collab.getAccessLevel());
        collabDTO.setAddedOn(collab.getAddedOn());

        return ResponseEntity.ok(collabDTO);
    }


    @PostMapping("/{boardId}/collabs")
    public ResponseEntity<?> addCollaborator(@PathVariable String boardId,
                                             @RequestHeader("Authorization") String requestTokenHeader,
                                             @RequestBody @Valid CollabRequest collabRequest) {
        String ownerId = getUserIdFromToken(requestTokenHeader);
        BoardEntity board = boardService.getBoardById(boardId);

        if (!board.getOwnerId().equals(ownerId)) {
            throw new UnauthorizedException("You are not authorized to add collaborators to this board.");
        }

        if (collabRequest.getAccessRight() == null ||
                !(collabRequest.getAccessRight().equals(AccessRight.READ) || collabRequest.getAccessRight().equals(AccessRight.WRITE))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid access right.");
        }

        Collaborator collaborator = collabService.addCollaborator(boardId, collabRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(collaborator);
    }


    private String getUserIdFromToken(String requestTokenHeader) {
        String token = requestTokenHeader.substring(7);
        return jwtTokenUtil.getUserIdFromToken(token);
    }

    public boolean isOwnerOrCollaborator(BoardEntity board, String userId) {
        // Check if the user is the owner
        if (board.getOwnerId().equals(userId)) {
            return true;
        }

        // Check if the user is a collaborator with READ or WRITE access
        Optional<Collaborator> collaborator = collabService.getCollaboratorByBoardIdAndCollaboratorId(board.getId(), userId);
        if (collaborator.isPresent()) {
            AccessRight accessRight = collaborator.get().getAccessLevel();
            // Return true if access level is either READ or WRITE
            return accessRight == AccessRight.READ || accessRight == AccessRight.WRITE;
        }

        // Return false if the user is neither an owner nor a collaborator with appropriate access
        return false;
    }




}
