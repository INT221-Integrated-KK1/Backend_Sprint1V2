package com.example.int221integratedkk1_backend.Controllers.Taskboard;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.example.int221integratedkk1_backend.DTOS.BoardRequest;
import com.example.int221integratedkk1_backend.Entities.Taskboard.BoardEntity;
import com.example.int221integratedkk1_backend.Services.Account.JwtTokenUtil;
import com.example.int221integratedkk1_backend.Services.Taskboard.BoardService;
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

    @GetMapping("")
    public ResponseEntity<List<BoardEntity>> getAllBoards(@RequestHeader("Authorization") String requestTokenHeader) {
        String ownerId = getUserIdFromToken(requestTokenHeader);
        List<BoardEntity> boards = boardService.getUserBoards(ownerId);
        return ResponseEntity.ok(boards);
    }

    @PostMapping("")
    public ResponseEntity<BoardEntity> createBoard(@RequestHeader("Authorization") String requestTokenHeader,
                                                   @Valid @RequestBody BoardRequest boardRequest) {
        String ownerId = getUserIdFromToken(requestTokenHeader); // Extract ownerId from the token

        // Create a new BoardEntity
        BoardEntity boardEntity = new BoardEntity();
        boardEntity.setBoardName(boardRequest.getBoardName()); //
        boardEntity.setOwnerId(ownerId);

        // generate a unique ID
        boardEntity.setId(generateNanoId());
        BoardEntity createdBoard = boardService.createBoard(ownerId, boardRequest);
        return ResponseEntity.status(201).body(createdBoard);
    }

    private String generateNanoId() {
        return NanoIdUtils.randomNanoId();
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
    private String getUserIdFromToken(String requestTokenHeader) {
        String token = requestTokenHeader.substring(7);
        return jwtTokenUtil.getUserIdFromToken(token);
    }
}
