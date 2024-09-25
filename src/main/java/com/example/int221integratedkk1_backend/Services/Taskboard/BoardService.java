package com.example.int221integratedkk1_backend.Services.Taskboard;

import com.example.int221integratedkk1_backend.DTOS.BoardRequest;
import com.example.int221integratedkk1_backend.Entities.Taskboard.BoardEntity;
import com.example.int221integratedkk1_backend.Entities.Taskboard.StatusEntity;
import com.example.int221integratedkk1_backend.Exception.DuplicateBoardException;
import com.example.int221integratedkk1_backend.Exception.ItemNotFoundException;
import com.example.int221integratedkk1_backend.Exception.UnauthorizedException;
import com.example.int221integratedkk1_backend.Exception.ValidateInputException;
import com.example.int221integratedkk1_backend.Repositories.Taskboard.BoardRepository;
import com.example.int221integratedkk1_backend.Repositories.Taskboard.StatusRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private StatusRepository statusRepository;

    public List<BoardEntity> getUserBoards(String ownerId) {
        return boardRepository.findByOwnerId(ownerId);
    }

//    public BoardEntity createBoard(String ownerId, @Valid BoardRequest boardRequest) throws DuplicateBoardException, ValidateInputException {// Check if the user already owns a board
//        if (boardRepository.existsByOwnerId(ownerId)) {
//            throw new DuplicateBoardException("User already owns a board.");
//        }
//        String boardName = boardRequest.getBoardName();
//        if (boardName == null || boardName.isEmpty() || boardName.length() > 120) {
//            throw new ValidateInputException("Board name is invalid.");
//        }
//
//        String boardId = generateUniqueBoardId();
//        BoardEntity boardEntity = new BoardEntity();
//        boardEntity.setId(boardId);
//        boardEntity.setBoardName(boardName);
//        boardEntity.setOwnerId(ownerId); //
//
//        return boardRepository.save(boardEntity);
//    }

    @Transactional
    public BoardEntity createBoard(String ownerId, BoardRequest boardRequest) {
        BoardEntity board = new BoardEntity();
        board.setId(generateUniqueBoardId() );
        board.setBoardName(boardRequest.getBoardName());
        board.setOwnerId(ownerId);
        boardRepository.save(board);

        // Initialize default statuses
        createDefaultStatuses(board);

        return board;
    }

    private void createDefaultStatuses(BoardEntity board) {
        String[] defaultStatusNames = {"No Status", "To Do", "Doing", "Done"};
        for (String statusName : defaultStatusNames) {
            StatusEntity status = new StatusEntity();
            status.setName(statusName);
            status.setBoard(board);
            statusRepository.save(status);
        }
    }



    private String generateUniqueBoardId() {
        String boardId;
        do {
            boardId = UUID.randomUUID().toString().substring(0, 10); //ต้องเปลี่ยนเป็น nanoId
        } while (boardRepository.existsById(boardId));
        return boardId;
    }

    public BoardEntity getBoardByIdAndOwner(String boardId, String ownerId) throws ItemNotFoundException, UnauthorizedException {
        Optional<BoardEntity> optionalBoard = boardRepository.findByIdAndOwnerId(boardId, ownerId);
        if (optionalBoard.isPresent()) {
            return optionalBoard.get();
        } else {
            throw new ItemNotFoundException("Board not found or user does not own the board.");
        }
    }

    public void deleteBoard(String boardId, String ownerId) throws ItemNotFoundException, UnauthorizedException {
        Optional<BoardEntity> optionalBoard = boardRepository.findByIdAndOwnerId(boardId, ownerId);
        if (optionalBoard.isPresent()) {
            boardRepository.delete(optionalBoard.get());
        } else {
            throw new ItemNotFoundException("Board not found or user does not have permission to delete it.");
        }
    }

    @Transactional
    public void updateBoard(String boardId, String ownerId, BoardEntity updatedBoard) throws ItemNotFoundException, UnauthorizedException {
        BoardEntity board = boardRepository.findByIdAndOwnerId(boardId, ownerId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found."));

        board.setBoardName(updatedBoard.getBoardName());

        boardRepository.save(board);
    }
}
