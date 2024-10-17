package com.example.int221integratedkk1_backend.Services.Taskboard;

import com.example.int221integratedkk1_backend.DTOS.CollabDTO;
import com.example.int221integratedkk1_backend.DTOS.CollabRequest;
import com.example.int221integratedkk1_backend.Entities.Taskboard.AccessRight;
import com.example.int221integratedkk1_backend.Entities.Taskboard.Collaborator;
import com.example.int221integratedkk1_backend.Entities.Taskboard.BoardEntity;
import com.example.int221integratedkk1_backend.Exception.CollaboratorAlreadyExistsException;
import com.example.int221integratedkk1_backend.Exception.ItemNotFoundException;
import com.example.int221integratedkk1_backend.Repositories.Account.UserRepository;
import com.example.int221integratedkk1_backend.Repositories.Taskboard.CollabRepository;
import com.example.int221integratedkk1_backend.Repositories.Taskboard.BoardRepository;
import com.example.int221integratedkk1_backend.Entities.Account.UsersEntity;
import com.example.int221integratedkk1_backend.Services.Account.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CollabService {

    @Autowired
    private CollabRepository collabRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BoardRepository boardRepository;

    public List<CollabDTO> getCollaborators(String boardId) {
        List<Collaborator> collaborators = collabRepository.findByBoardId(boardId);
        return collaborators.stream().map(collab -> {
            CollabDTO collaboratorDTO = new CollabDTO();
            collaboratorDTO.setOid(collab.getCollabsId());
            collaboratorDTO.setName(collab.getCollabsName());
            collaboratorDTO.setEmail(collab.getCollabsEmail());
            collaboratorDTO.setAccessRight(collab.getAccessLevel());
            collaboratorDTO.setAddedOn(collab.getAddedOn());
            return collaboratorDTO;
        }).collect(Collectors.toList());
    }

//    public boolean isCollaborator(String boardId, String userId) {
//        return collabRepository.existsByBoardIdAndCollabsId(boardId, userId);
//    }


    public Optional<Collaborator> getCollaboratorByBoardIdAndCollabId(String boardId, String collabId) {
        return collabRepository.findByBoardIdAndCollabsId(boardId, collabId);
    }
    public Collaborator addCollaborator(String boardId, CollabRequest collabRequest)
            throws CollaboratorAlreadyExistsException, ItemNotFoundException {

        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with ID: " + boardId));

        UsersEntity user = userService.findUserByEmail(collabRequest.getEmail());
        if (user == null) {
            throw new ItemNotFoundException("User not found with email: " + collabRequest.getEmail());
        }

        if (collabRepository.existsByBoardIdAndCollabsId(boardId, user.getOid())) {
            throw new CollaboratorAlreadyExistsException("Collaborator already exists for this board.");
        }


        if (board.getOwnerId().equals(user.getOid())) {
            throw new CollaboratorAlreadyExistsException("Board owner cannot be added as a collaborator.");
        }


        Collaborator collaborator = new Collaborator();
        collaborator.setBoardId(boardId);
        collaborator.setCollabsId(user.getOid());
        collaborator.setCollabsName(user.getName());
        collaborator.setCollabsEmail(user.getEmail());
        collaborator.setAccessLevel(collabRequest.getAccessRight());
        collaborator.setAddedOn(new Timestamp(System.currentTimeMillis()));

        collaborator.setOwnerId(board.getOwnerId());

        collabRepository.save(collaborator);

        return collaborator;
    }

    public List<BoardEntity> getBoardsWhereUserIsCollaborator(String userId) {
        List<Collaborator> collaborators = collabRepository.findByCollabsId(userId);
        List<String> boardIds = collaborators.stream().map(Collaborator::getBoardId).collect(Collectors.toList());
        return boardRepository.findAllById(boardIds);
    }

//    public boolean isCollaboratorWithAccess(String boardId, String userId, AccessRight requiredAccessRight) {
//        Optional<Collaborator> collaborator = collabRepository.findByBoardIdAndCollaboratorId(boardId, userId);
//
//        return collaborator.isPresent() && collaborator.get().getAccessLevel().equals(requiredAccessRight);
//    }
public Optional<Collaborator> getCollaboratorByBoardIdAndCollaboratorId(String boardId, String userId) {
    return collabRepository.findByBoardIdAndCollabsId(boardId, userId);
}

    public boolean isCollaborator(String boardId, String userId) {
        return collabRepository.existsByBoardIdAndCollabsId(boardId, userId);
    }


}
