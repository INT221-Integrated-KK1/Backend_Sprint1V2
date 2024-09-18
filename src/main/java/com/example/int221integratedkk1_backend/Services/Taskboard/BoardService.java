package com.example.int221integratedkk1_backend.Services.Taskboard;

import com.example.int221integratedkk1_backend.DTOS.BoardRequest;
import com.example.int221integratedkk1_backend.Entities.Taskboard.BoardEntity;
import com.example.int221integratedkk1_backend.Exception.DuplicateBoardException;
import com.example.int221integratedkk1_backend.Exception.ItemNotFoundException;
import com.example.int221integratedkk1_backend.Exception.UnauthorizedException;
import com.example.int221integratedkk1_backend.Exception.ValidateInputException;
import com.example.int221integratedkk1_backend.Repositories.Taskboard.BoardRepository;
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

    public List<BoardEntity> getUserBoards(String ownerId) {
        return boardRepository.findByOwnerId(ownerId);
    }

    public BoardEntity createBoard(String ownerId, @Valid BoardRequest boardRequest) throws DuplicateBoardException, ValidateInputException {// Check if the user already owns a board
        if (boardRepository.existsByOwnerId(ownerId)) {
            throw new DuplicateBoardException("User already owns a board.");
        }
        String boardName = boardRequest.getBoardName();
        if (boardName == null || boardName.isEmpty() || boardName.length() > 120) {
            throw new ValidateInputException("Board name is invalid.");
        }

        String boardId = generateUniqueBoardId();
        BoardEntity boardEntity = new BoardEntity();
        boardEntity.setId(boardId);
        boardEntity.setBoardName(boardName);
        boardEntity.setOwnerId(ownerId); //

        return boardRepository.save(boardEntity);
    }

    private String generateUniqueBoardId() {
        String boardId;
        do {
            boardId = UUID.randomUUID().toString().substring(0, 10); // Using UUID with length 10
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
