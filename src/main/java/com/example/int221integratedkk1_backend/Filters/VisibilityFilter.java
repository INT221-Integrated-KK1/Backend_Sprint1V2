package com.example.int221integratedkk1_backend.Filters;

import com.example.int221integratedkk1_backend.Entities.Account.Visibility;
import com.example.int221integratedkk1_backend.Entities.Taskboard.BoardEntity;
import com.example.int221integratedkk1_backend.Repositories.Taskboard.BoardRepository;
import com.example.int221integratedkk1_backend.Services.Account.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class VisibilityFilter extends OncePerRequestFilter {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (requestURI.matches("/v3/boards/([^/]+)(/.*)?")) {
            String boardId = requestURI.split("/")[3];
            Optional<BoardEntity> boardOptional = boardRepository.findById(boardId);

            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No token provided");
                return;
            }

            String jwtToken = authorizationHeader.substring(7);
            try {
                String userIdFromToken = jwtTokenUtil.getUserIdFromToken(jwtToken);

                if (boardOptional.isPresent()) {
                    BoardEntity board = boardOptional.get();
                    if (!hasPermission(board, userIdFromToken)) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to access this resource.");
                        return;
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Board not found.");
                    return;
                }
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT token.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean hasPermission(BoardEntity board, String userId) {
        if (board.getVisibility() == Visibility.PUBLIC) {
            return true;
        }
        return board.getOwnerId().equals(userId);
    }
}