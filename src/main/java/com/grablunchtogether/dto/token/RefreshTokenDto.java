package com.grablunchtogether.dto.token;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RefreshTokenDto {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(value = "리프레시토큰 Request")
    public static class Request {
        private String refresh_token;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("리프레시토큰 Dto")
    public static class Dto {
        private String email;
        private String token;

        public static Dto from(String email, String refreshToken) {
            return Dto.builder()
                    .email(email)
                    .token(refreshToken)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("리프레시토큰 AccessToken")
    public static class AccessToken {
        private String accessToken;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(value = "리프레시토큰 Response")
    public static class Response {
        private String accessToken;

        public static Response from(RefreshTokenDto.AccessToken tokenRefreshDto) {
            return Response.builder().accessToken(tokenRefreshDto.getAccessToken()).build();
        }
    }
}