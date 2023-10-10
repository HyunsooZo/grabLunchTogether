package com.grablunchtogether.config;

import com.grablunchtogether.dto.token.TokenDto;
import com.grablunchtogether.enums.UserRole;
import com.grablunchtogether.service.UserTokenDetailService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.grablunchtogether.enums.UserRole.valueOf;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
    private final UserTokenDetailService userDetailsService;

    @Value("${token.key}")
    private String issuer;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public String issuingAccessToken(TokenDto.TokenIssuanceDto tokenTokenIssuanceDto) {
        Claims claims = Jwts.claims().setSubject(tokenTokenIssuanceDto.getId().toString());
        claims.put("email", tokenTokenIssuanceDto.getEmail());
        claims.put("userRole", tokenTokenIssuanceDto.getUserRole().getRoleName());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(issuer)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // 유효기간 1시간
                .setExpiration(new Date(System.currentTimeMillis() + (60 * 60 * 1000)))
                .signWith(secretKey)
                .compact();
    }

    public String issuingRefreshToken(String email) {
        return Jwts.builder()
                .claim("email", email)
                .setIssuer(issuer)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                //유효기간 1일
                .setExpiration(new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)))
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException ex) {
            return false;
        }
    }

    public Long getIdFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return Long.parseLong(Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
    }

    public String getEmailFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("email", String.class);
    }

    public Authentication getAuthentication(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String email = claims.get("email", String.class);
        UserRole userRole = valueOf(claims.get("userRole", String.class));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        List<GrantedAuthority> authorities = new ArrayList<>(userDetails.getAuthorities());
        authorities.addAll(userRole.getAuthorities()); // 추가된 역할 권한

        return new UsernamePasswordAuthenticationToken(
                userDetails, "", authorities
        );
    }
}