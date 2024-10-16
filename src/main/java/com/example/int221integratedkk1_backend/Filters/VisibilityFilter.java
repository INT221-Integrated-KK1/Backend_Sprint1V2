package com.example.int221integratedkk1_backend.Filters;

import com.example.int221integratedkk1_backend.Entities.Account.Visibility;
import com.example.int221integratedkk1_backend.Entities.Taskboard.BoardEntity;
import com.example.int221integratedkk1_backend.Repositories.Taskboard.BoardRepository;
import com.example.int221integratedkk1_backend.Services.Account.JwtTokenUtil;
import com.example.int221integratedkk1_backend.Services.Taskboard.CollabService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
public class VisibilityFilter extends OncePerRequestFilter {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private CollabService collabService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        if (requestURI.matches("/v3/boards/([^/]+)(/.*)?")) {
            String boardId = requestURI.split("/")[3];
            Optional<BoardEntity> boardOptional = boardRepository.findById(boardId);

            if (boardOptional.isPresent()) {
                BoardEntity board = boardOptional.get();

                // Public board Allow GET requests without authentication
                if (board.getVisibility() == Visibility.PUBLIC && method.equals("GET")) {
                    filterChain.doFilter(request, response);
                    return;
                }

                // Private board: Require authentication and check if user is owner or collaborator
                String authorizationHeader = request.getHeader("Authorization");

                if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                    String jwtToken = authorizationHeader.substring(7);
                    try {
                        String userIdFromToken = jwtTokenUtil.getUserIdFromToken(jwtToken);

                        // User is the board owner or collaborator, allow access
                        if (board.getOwnerId().equals(userIdFromToken) || collabService.isCollaborator(boardId, userIdFromToken)) {
                            filterChain.doFilter(request, response);
                            return;
                        }

                        // If the user is neither the owner nor a collaborator, block access
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to access this resource.");
                        return;

                    } catch (ExpiredJwtException e) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired.");
                        return;
                    } catch (Exception e) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT token.");
                        return;
                    }
                } else {
                    // No token provided for private board
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication is required.");
                    return;
                }
            } else {
                // Board not found
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Board not found.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
