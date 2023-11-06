package com.grablunchtogether.dto;

import com.grablunchtogether.domain.User;
import com.grablunchtogether.enums.UserRole;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class TokenDto {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("토큰발행 Dto")
    public static class TokenIssuanceDto {
        private Long id;
        private String email;
        private UserRole userRole;

        public static TokenIssuanceDto from(User user) {
            return TokenIssuanceDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .userRole(user.getUserRole())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("토큰 Dto")
    public static class Dto {
        private String accessToken;
        private String refreshToken;
        private Long id;
        private String userEmail;
        private String userName;
        private String userPhoneNumber;
        private double userRate;
        private String company;

        public static Dto from(User user, String accessToken, String refreshToken) {
            return Dto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .id(user.getId())
                    .userEmail(user.getEmail())
                    .userName(user.getName())
                    .userRate(user.getRate())
                    .userPhoneNumber(user.getPhoneNumber())
                    .company(user.getCompany())
                    .build();
        }
    }
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("로그인 응답")
    public static class Response {
        private String accessToken;
        private String refreshToken;
        private Long id;
        private String userEmail;
        private String userName;
        private String userPhoneNumber;
        private double userRate;
        private String company;

        public static Response from(Dto dto) {
            return Response.builder()
                    .accessToken(dto.getAccessToken())
                    .refreshToken(dto.getRefreshToken())
                    .id(dto.getId())
                    .userEmail(dto.getUserEmail())
                    .userName(dto.getUserName())
                    .userRate(dto.getUserRate())
                    .userPhoneNumber(dto.getUserPhoneNumber())
                    .company(dto.getCompany())
                    .build();
        }
    }
}
