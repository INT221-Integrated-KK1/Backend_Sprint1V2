package com.example.int221integratedkk1_backend.Filters;

import com.example.int221integratedkk1_backend.Entities.Account.Visibility;
import com.example.int221integratedkk1_backend.Entities.Taskboard.BoardEntity;
import com.example.int221integratedkk1_backend.Repositories.Taskboard.BoardRepository;
import com.example.int221integratedkk1_backend.Services.Account.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class VisibilityFilter extends OncePerRequestFilter {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();  // Get the HTTP method (GET, POST, etc.)

        if (requestURI.matches("/v3/boards/([^/]+)(/.*)?")) {
            String boardId = requestURI.split("/")[3];
            Optional<BoardEntity> boardOptional = boardRepository.findById(boardId);

            if (boardOptional.isPresent()) {
                BoardEntity board = boardOptional.get();

                // Allow access if the board is public and it's a GET request
                log.info("---------------------------------------------------------------------------------------------------");
                log.info(board.getVisibility().toString());
                log.info(method.toString());
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    if (board.getVisibility() == Visibility.PUBLIC && method.equals("GET")) {
                        List<GrantedAuthority> authorities = new ArrayList<>();
                        authorities.add(new SimpleGrantedAuthority("PUBLIC")); // สิทธิ์สำหรับคนที่ไม่ได้ Login แล้วเข้าไปดู board ได้
                        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(null, null, authorities));

                    } else if (board.getVisibility() == Visibility.PRIVATE && method.equals("GET")) {
                        List<GrantedAuthority> authorities = new ArrayList<>();
                        authorities.add(new SimpleGrantedAuthority("Anonymous"));
                        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(null, null, authorities));

                    }
                } else if (SecurityContextHolder.getContext().getAuthentication() != null && !method.equals("GET")) {
                    String authorizationHeader = request.getHeader("Authorization");




                    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
                        String jwtToken = authorizationHeader.substring(7);
                        try {
                            String userIdFromToken = jwtTokenUtil.getUserIdFromToken(jwtToken);

                            if (boardOptional.isPresent()) {
                                if (!board.getOwnerId().equals(userIdFromToken)) {
                                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to access this resource.");
                                    return;
                                }
                            } else {
                                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Board not found.");
                                return;
                            }
                        } catch (ExpiredJwtException e) {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired.");
                            return;
                        } catch (MalformedJwtException e) {
                            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Malformed JWT token.");
                            return;
                        } catch (SignatureException e) {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT signature.");
                            return;
                        } catch (IllegalArgumentException e) {
                            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "JWT claims string is empty.");
                            return;
                        } catch (Exception e) {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT token.");
                            return;
                        }
                    }
                }
            }
            else if (!boardOptional.isPresent() && method.equals("GET")){
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Board not found.");
                return;
            }

            String authorizationHeader = request.getHeader("Authorization");

//            // Check if no token is provided, handle as unauthorized unless board is public for GET
//            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No token provided");
//                return;
//            }

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
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
                } catch (ExpiredJwtException e) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired.");
                    return;
                } catch (MalformedJwtException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Malformed JWT token.");
                    return;
                } catch (SignatureException e) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT signature.");
                    return;
                } catch (IllegalArgumentException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "JWT claims string is empty.");
                    return;
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT token.");
                    return;
                }
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
