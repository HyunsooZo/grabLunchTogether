package com.grablunchtogether.service;

import com.grablunchtogether.domain.FavoriteUser;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.FavoriteUserDto;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.repository.FavoriteUserRepository;
import com.grablunchtogether.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.grablunchtogether.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class FavoriteUserService {
    private final FavoriteUserRepository favoriteUserRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addFavoriteUser(FavoriteUserDto.Request favoriteUserRequest,
                                Long userId,
                                Long otherUserId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        User favoriteUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        favoriteUserRepository.save(
                FavoriteUser.builder()
                        .nickName(favoriteUserRequest.getNickName())
                        .userId(user)
                        .favoriteUserId(favoriteUser)
                        .build()
        );
    }

    @Transactional
    public void editFavoriteUser(FavoriteUserDto.Request favoriteUserEditRequest,
                                 Long userId,
                                 Long favoriteUserId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        FavoriteUser favoriteUser = favoriteUserRepository.findById(favoriteUserId)
                .orElseThrow(() -> new CustomException(FAVORITE_USER_NOT_FOUND));

        if (!favoriteUser.getUserId().equals(user)) {
            throw new CustomException(NOT_PERMITTED);
        }

        favoriteUser.setNickname(favoriteUserEditRequest);

        favoriteUserRepository.save(favoriteUser);
    }

    @Transactional
    public void deleteFavoriteUser(Long userId, Long favoriteUserId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        FavoriteUser favoriteUser = favoriteUserRepository.findById(favoriteUserId)
                .orElseThrow(() -> new CustomException(FAVORITE_USER_NOT_FOUND));

        if (!favoriteUser.getUserId().equals(user)) {
            throw new CustomException(NOT_PERMITTED);
        }

        favoriteUserRepository.delete(favoriteUser);
    }

    @Transactional
    public List<FavoriteUserDto.Dto> listFavoriteUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        List<FavoriteUser> favoriteUsers = favoriteUserRepository.findByUserId(user);

        return favoriteUsers.stream()
                .map(FavoriteUserDto.Dto::of)
                .collect(Collectors.toList());
    }
}
