package com.example.int221integratedkk1_backend.Controllers.Account;

import com.example.int221integratedkk1_backend.DTOS.JwtRequestUser;
import com.example.int221integratedkk1_backend.DTOS.JwtResponseToken;
import com.example.int221integratedkk1_backend.Entities.Account.AuthUser;
import com.example.int221integratedkk1_backend.Entities.Account.UsersEntity;
import com.example.int221integratedkk1_backend.Exception.UnauthorizedException;
import com.example.int221integratedkk1_backend.Services.Account.JwtTokenUtil;
import com.example.int221integratedkk1_backend.Services.Account.JwtUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:5173", "http://ip23kk1.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th:8080", "http://ip23kk1.sit.kmutt.ac.th:8080"})
//@CrossOrigin(origins = {"http://localhost:5173", "https://ip23kk1.sit.kmutt.ac.th", "https://intproj23.sit.kmutt.ac.th", "https://intproj23.sit.kmutt.ac.th:8080", "https://ip23kk1.sit.kmutt.ac.th:8080"})

@RestController
public class AuthenticationController {
    @Autowired
    JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    AuthenticationManager authenticationManager;


    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody JwtRequestUser user) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenUtil.generateToken(userDetails);
            String refreshtoken = jwtTokenUtil.generateRefreshToken(userDetails);
            return ResponseEntity.ok(new JwtResponseToken(token,refreshtoken));
        } catch (UnauthorizedException e) {
            throw new UnauthorizedException("Username or Password is incorrect.");
        } catch (Exception e) {
            throw new UnauthorizedException("Username or Password is incorrect.");
        }
    }

    @GetMapping("/validate-token")
    public ResponseEntity<Object> validateToken(@RequestHeader("Authorization") String requestTokenHeader) {
        Claims claims = null;
        String jwtToken = null;
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                claims = jwtTokenUtil.getAllClaimsFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token has expired");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "JWT Token does not begin with Bearer String");
        }
        return ResponseEntity.ok(claims);
    }

    // pbi 22 เพิ่ม method post มาอีกอันนึง เอาไว้รับ refresh token จาก frontend
    // เช็คว่า token 30 นาที หมดอายุ แล้ว gen token ใหม่ขึ้นมา (เป็น token 30 นาที)
    // อย่าลืมเช็ค oid ให้มันตรงกัน
//
    @PostMapping("/token")
    public ResponseEntity<Object> refreshAccessToken(@RequestHeader("x-refresh-token") String requestTokenHeader) {
        String refreshToken = requestTokenHeader;
        Claims claims = null;

        try {
            // Extract claims from the refresh token
            claims = jwtTokenUtil.getAllClaimsFromToken(refreshToken);

            // Ensure refresh token has not expired
            if (jwtTokenUtil.isTokenExpired(refreshToken)) {
                throw new UnauthorizedException("Refresh token has expired");
            }

            // Get the oid (userId) from claims
            String oidFromRefreshToken = claims.get("oid", String.class);
            if (oidFromRefreshToken == null) {
                throw new UnauthorizedException("Invalid refresh token: oid is missing");
            }

            // Check that the user exists and has a matching oid
            UserDetails userDetails = jwtUserDetailsService.loadUserByOid(oidFromRefreshToken);
            if (userDetails == null) {
                // User no longer exists
                throw new UnauthorizedException("Invalid refresh token: User does not exist");
            }

            AuthUser authUser = (AuthUser) userDetails;
            if (!authUser.getOid().equals(oidFromRefreshToken)) {
                throw new UnauthorizedException("Invalid refresh token: OID mismatch");
            }

            // Generate a new access token
            String newAccessToken = jwtTokenUtil.generateToken(userDetails);
            return ResponseEntity.ok(new JwtResponseToken(newAccessToken));

        } catch (ExpiredJwtException e) {
           // logger.error("Refresh token has expired", e);
            throw new UnauthorizedException("Refresh token has expired");
        } catch (UnauthorizedException e) {
            // Catch and log the unauthorized exceptions explicitly
            //logger.error("Unauthorized: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            // Handle generic exceptions
            //logger.error("Error processing refresh token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error occurred");
        }
    }

}
