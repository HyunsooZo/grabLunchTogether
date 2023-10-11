package com.grablunchtogether.dto;

import com.grablunchtogether.domain.FavoriteUser;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


public class FavoriteUserDto {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("FavoriteUser 요청")
    public static class Request {
        private String nickName;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("FavoriteUser Dto")
    public static class Dto {
        private Long id;
        private Long userId;
        private Long favoriteUserId;
        private String userEmail;
        private String favoriteUserEmail;
        private String nickName;
        private LocalDateTime registeredAt;

        public static Dto of(FavoriteUser favoriteUser) {
            return Dto.builder()
                    .id(favoriteUser.getId())
                    .userId(favoriteUser.getUserId().getId())
                    .favoriteUserId(favoriteUser.getFavoriteUserId().getId())
                    .userEmail(favoriteUser.getUserId().getUserEmail())
                    .favoriteUserEmail(favoriteUser.getFavoriteUserId().getUserEmail())
                    .nickName(favoriteUser.getNickName())
                    .registeredAt(favoriteUser.getRegisteredAt())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("FavoriteUser 응답")
    public static class Response {
        private List<Dto> favoriteUsers;

        public static Response of(List<Dto> favoriteUsers) {
            return Response.builder().favoriteUsers(favoriteUsers).build();
        }
    }
}
