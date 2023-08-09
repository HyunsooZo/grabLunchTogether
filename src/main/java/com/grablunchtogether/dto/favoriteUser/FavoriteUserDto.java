package com.grablunchtogether.dto.favoriteUser;

import com.grablunchtogether.domain.FavoriteUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteUserDto {
    private Long id;
    private Long userId;
    private Long favoriteUserId;
    private String userEmail;
    private String favoriteUserEmail;
    private String nickName;
    private LocalDateTime registeredAt;

    public static FavoriteUserDto of(FavoriteUser favoriteUser) {
        return FavoriteUserDto.builder()
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
