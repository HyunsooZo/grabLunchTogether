package com.grablunchtogether.configuration.springSecurity;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.grablunchtogether.dto.TokenDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${token.key}")
    private String secretKey;

    public String issuingToken(TokenDto tokenDto) {
        Date expiration = java.sql.Timestamp.valueOf(
                LocalDateTime.now().plusHours(3)
        );

        String token = JWT.create()
                .withExpiresAt(expiration)
                .withClaim("user_id", tokenDto.getClaim())
                .withSubject(tokenDto.getSubject())
                .withIssuer(tokenDto.getIssuer())
                .sign(Algorithm.HMAC512(secretKey.getBytes()));

        return token;
    }

    public String getIssuer(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String issuer = "";
        try {
            issuer = JWT.require(Algorithm.HMAC512(secretKey.getBytes()))
                    .build()
                    .verify(token)
                    .getIssuer();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return issuer;
    }

    public boolean verifyToken(String token) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        try {
            Algorithm algorithm = Algorithm.HMAC512(secretKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true; // 토큰 검증 성공
        } catch (JWTVerificationException e) {
            return false; // 토큰 검증 실패
        }
    }
}