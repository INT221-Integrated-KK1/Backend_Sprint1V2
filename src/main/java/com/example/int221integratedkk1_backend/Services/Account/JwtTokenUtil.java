package com.example.int221integratedkk1_backend.Services.Account;

import com.example.int221integratedkk1_backend.Entities.Account.AuthUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    // ระยะเวลาใช้งานของ access token (30 นาที)
    @Value("#{${jwt.max-token-interval-hour}*60*60*1000}")
    private long JWT_TOKEN_VALIDITY;

    // ระยะเวลาใช้งานของ refresh token (24 ชั่วโมง)
    @Value("#{${jwt.max-token-interval-hour}*48*60*60*1000}")  // 24 ชั่วโมง (48 * 0.5 ชั่วโมง)
    private long JWT_REFRESH_TOKEN_VALIDITY;



    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // ดึง username จาก token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // ดึง userId จาก token โดยใช้ key "oid"
    public String getUserIdFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("oid", String.class));
    }

    // ดึงวันที่หมดอายุของ token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // ฟังก์ชันเพื่อดึงข้อมูล claim อื่นๆ จาก token
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }


    // ดึง claim ทั้งหมดจาก token โดยใช้ SECRET_KEY เพื่อถอดรหัส
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    // ตรวจสอบว่า token หมดอายุหรือยัง
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // สร้าง access token
    public String generateToken(UserDetails userDetails) {
        AuthUser authUser = (AuthUser) userDetails;
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", authUser.getName());  // เก็บข้อมูลชื่อผู้ใช้
        claims.put("oid", authUser.getOid());    // เก็บ userId ใน key 'oid'
        claims.put("email", authUser.getEmail());  // เก็บข้อมูลอีเมล
        claims.put("role", authUser.getRole().name());  // เก็บบทบาทของผู้ใช้
        return doGenerateToken(claims, userDetails.getUsername(), JWT_TOKEN_VALIDITY);  // ใช้เวลาหมดอายุของ access token
    }

    // สร้าง refresh token
    public String generateRefreshToken(UserDetails userDetails) {
        AuthUser authUser = (AuthUser) userDetails;
        Map<String, Object> claims = new HashMap<>();
        claims.put("oid", authUser.getOid());    // เก็บ userId ใน key 'oid'
        return doGenerateToken(claims, userDetails.getUsername(), JWT_REFRESH_TOKEN_VALIDITY);  // ใช้เวลาหมดอายุของ refresh token
    }

    // ฟังก์ชันที่ใช้สร้าง token ไม่ว่าจะเป็น access หรือ refresh token ขึ้นอยู่กับเวลาที่กำหนด
    private String doGenerateToken(Map<String, Object> claims, String subject, long validity) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer("https://intproj23.sit.kmutt.ac.th/kk1/")  // กำหนด issuer
                .setIssuedAt(new Date(System.currentTimeMillis()))    // เวลาที่ออก token
                .setExpiration(new Date(System.currentTimeMillis() + validity))  // เวลาหมดอายุของ token
                .signWith(signatureAlgorithm, SECRET_KEY)  // เข้ารหัส token ด้วย SECRET_KEY
                .compact();
    }

    // ตรวจสอบว่า token ที่ได้รับมานั้นถูกต้องและไม่หมดอายุ
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
