package com.grablunchtogether.service.favoriteUser;

import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.FavoriteUser;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.favoriteUser.FavoriteUserDto;
import com.grablunchtogether.dto.favoriteUser.FavoriteUserInput;
import com.grablunchtogether.repository.FavoriteUserRepository;
import com.grablunchtogether.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.grablunchtogether.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class FavoriteUserService{
    private final FavoriteUserRepository favoriteUserRepository;
    private final UserRepository userRepository;

    @Transactional
    public ServiceResult addFavoriteUser(FavoriteUserInput favoriteUserInput,
                                         Long userId,
                                         Long otherUserId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        User favoriteUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        favoriteUserRepository.save(
                FavoriteUser.builder()
                        .nickName(favoriteUserInput.getNickName())
                        .userId(user)
                        .favoriteUserId(favoriteUser)
                        .build()
        );

        return ServiceResult.success("즐겨찾기 유저 추가가 완료되었습니다.");
    }

    @Transactional
    public ServiceResult editFavoriteUser(FavoriteUserInput favoriteUserEditInput,
                                          Long userId,
                                          Long favoriteUserId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        FavoriteUser favoriteUser = favoriteUserRepository.findById(favoriteUserId)
                .orElseThrow(()-> new CustomException(CONTENT_NOT_FOUND));

        if(!favoriteUser.getUserId().equals(user)){
            throw new CustomException(NOT_PERMITTED);
        }

        favoriteUser.edit(favoriteUserEditInput);

        favoriteUserRepository.save(favoriteUser);

        return ServiceResult.success("즐겨찾기 유저 정보가 수정되었습니다.");
    }

    @Transactional
    public ServiceResult deleteFavoriteUser(Long userId, Long favoriteUserId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        FavoriteUser favoriteUser = favoriteUserRepository.findById(favoriteUserId)
                .orElseThrow(()-> new CustomException(CONTENT_NOT_FOUND));

        if(!favoriteUser.getUserId().equals(user)){
            throw new CustomException(NOT_PERMITTED);
        }

        favoriteUserRepository.delete(favoriteUser);

        return ServiceResult.success("즐겨찾는 유저 삭제가 완료되었습니다.");
    }

    @Transactional
    public ServiceResult listFavoriteUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        List<FavoriteUser> listEntity = favoriteUserRepository.findByUserId(user);

        if (listEntity.isEmpty()) {
            throw new CustomException(CONTENT_NOT_FOUND);
        }

        List<FavoriteUserDto> result = new ArrayList<>();

        listEntity.forEach(favoriteUser -> {
            result.add(FavoriteUserDto.of(favoriteUser));
        });

        return ServiceResult.success("목록 수신 성공", result);
    }
}
