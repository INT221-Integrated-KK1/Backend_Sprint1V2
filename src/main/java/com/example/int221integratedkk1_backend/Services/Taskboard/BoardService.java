package com.example.int221integratedkk1_backend.Services.Taskboard;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.example.int221integratedkk1_backend.DTOS.BoardRequest;
import com.example.int221integratedkk1_backend.Entities.Account.Visibility;
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
import java.util.Random;
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


    @Transactional
    public BoardEntity createBoard(String ownerId, BoardRequest boardRequest) {
        BoardEntity board = new BoardEntity();
        board.setId(generateUniqueBoardId());
        board.setBoardName(boardRequest.getName());
        board.setOwnerId(ownerId);

        board.setVisibility(Visibility.PRIVATE);

        System.out.println("Visibility before save: " + board.getVisibility());
        boardRepository.save(board);
        System.out.println("Visibility after save: " + board.getVisibility());

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
        char[] alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

        int size = 10;

        String boardId;
        do {
            boardId = NanoIdUtils.randomNanoId(new Random(), alphabet, size);
        } while (boardRepository.existsById(boardId));

        return boardId;
    }

    public BoardEntity getBoardByIdAndOwner(String boardId, String ownerId) throws ItemNotFoundException, UnauthorizedException {
        Optional<BoardEntity> optionalBoard = boardRepository.findByIdAndOwnerId(boardId, ownerId);
        if (optionalBoard.isPresent()) {
            return optionalBoard.get();
        } else {
            throw new ItemNotFoundException("Board not found or user does not an owner");
        }
    }

    // แก้ pbi20 เพิ่มมา
    public void deleteBoard(String boardId, String ownerId) throws ItemNotFoundException, UnauthorizedException {
        Optional<BoardEntity> optionalBoard = boardRepository.findByIdAndOwnerId(boardId, ownerId);
        if (optionalBoard.isPresent()) {
            if (!optionalBoard.get().getOwnerId().equals(ownerId)) {
                throw new UnauthorizedException("You are not the owner of this board.");
            }
            boardRepository.delete(optionalBoard.get());
        } else {
            throw new ItemNotFoundException("Board not found or user does not own the board.");
        }
    }


    @Transactional
    public void updateBoard(String boardId, String ownerId, BoardEntity updatedBoard) throws ItemNotFoundException, UnauthorizedException {
        BoardEntity board = boardRepository.findByIdAndOwnerId(boardId, ownerId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found"));

        board.setBoardName(updatedBoard.getBoardName());

        boardRepository.save(board);
    }

    public BoardEntity getBoardById(String boardId) throws ItemNotFoundException {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found"));
    }

    @Transactional
    public void updateBoardVisibility(BoardEntity board) {
        boardRepository.save(board);
    }
}