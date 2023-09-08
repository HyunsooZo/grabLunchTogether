package com.grablunchtogether.config;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.*;

@AllArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private JwtTokenProvider jwtTokenProvider;
    private static final String[] ALL_WHITELIST = {
            "/api/users/signup",
            "/api/users/signup/ocr",
            "/api/users/login",
            "/api/users/password/reset",
            "/api/users/image",
            "/api/auth/refresh",
            "/otp/verification",
            "/swagger-ui/index.html",
            "/swagger-ui/**",
            "/v2/api-docs",
            "/swagger-resources/**",
            "/webjars/**"
    };

    //화이트리스트(필터링 대상인지 아닌지 판별)
    private boolean isFilterCheck(String requestURI) {
        return !PatternMatchUtils.simpleMatch(ALL_WHITELIST, requestURI);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException {

        String token = extractTokenFromRequest(request);

        try {
            // 화이트리스트에 있는 경우에는 필터링을 건너뛰어서 다음 필터로 진행
            if (isFilterCheck(request.getRequestURI())) {
                // 화이트리스트에 없는 경우에만 검증 처리
                if (token != null) {
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(jwtTokenProvider.getAuthentication(token));
                }
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " prefix 지우기
        }
        return null;
    }
}
