package com.example.int221integratedkk1_backend.Controllers.Taskboard;

import com.example.int221integratedkk1_backend.Entities.Taskboard.StatusEntity;
import com.example.int221integratedkk1_backend.Services.Account.JwtTokenUtil;
import com.example.int221integratedkk1_backend.Services.Taskboard.StatusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/v3/boards/{boardId}/statuses")
@CrossOrigin(origins = {"http://localhost:5173", "http://ip23kk1.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th:8080", "http://ip23kk1.sit.kmutt.ac.th:8080"})
public class StatusController {

//    @Autowired
//    private JwtTokenUtil jwtTokenUtil;
//
//    @Autowired
//    private StatusService statusService;
//
//    @GetMapping("")
//    public ResponseEntity<List<StatusEntity>> getAllStatuses(@PathVariable String boardId,
//                                                             @RequestHeader("Authorization") String requestTokenHeader) {
//        String userName = getUserNameFromToken(requestTokenHeader);
//        List<StatusEntity> statuses = statusService.getStatusesByBoard(boardId, userName);
//        return ResponseEntity.ok(statuses);
//    }
//
//    @PostMapping("")
//    public ResponseEntity<StatusEntity> createStatus(@PathVariable String boardId,
//                                                     @RequestHeader("Authorization") String requestTokenHeader,
//                                                     @Valid @RequestBody StatusEntity statusEntity) {
//        String userName = getUserNameFromToken(requestTokenHeader);
//        StatusEntity createdStatus = statusService.createStatus(boardId, userName, statusEntity);
//        return ResponseEntity.status(201).body(createdStatus);
//    }
//
//    @GetMapping("/{statusId}")
//    public ResponseEntity<StatusEntity> getStatusById(@PathVariable String boardId,
//                                                      @PathVariable Integer statusId,
//                                                      @RequestHeader("Authorization") String requestTokenHeader) {
//        String userName = getUserNameFromToken(requestTokenHeader);
//        StatusEntity status = statusService.getStatusByIdAndBoard(statusId, boardId, userName);
//        return ResponseEntity.ok(status);
//    }
//
//    @PutMapping("/{statusId}")
//    public ResponseEntity<String> updateStatus(@PathVariable String boardId,
//                                               @PathVariable Integer statusId,
//                                               @RequestHeader("Authorization") String requestTokenHeader,
//                                               @Valid @RequestBody StatusEntity updatedStatus) {
//        String userName = getUserNameFromToken(requestTokenHeader);
//        String result = statusService.updateStatus(statusId, boardId, userName, updatedStatus);
//        return ResponseEntity.ok(result);
//    }
//
//    @DeleteMapping("/{statusId}")
//    public ResponseEntity<String> deleteStatus(@PathVariable String boardId,
//                                               @PathVariable Integer statusId,
//                                               @RequestHeader("Authorization") String requestTokenHeader) {
//        String userName = getUserNameFromToken(requestTokenHeader);
//        statusService.deleteStatus(statusId, boardId, userName);
//        return ResponseEntity.ok("Status deleted successfully");
//    }
//
//    private String getUserNameFromToken(String requestTokenHeader) {
//        String token = requestTokenHeader.substring(7);
//        return jwtTokenUtil.getUsernameFromToken(token);
//    }
}
