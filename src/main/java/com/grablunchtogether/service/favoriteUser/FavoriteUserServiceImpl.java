package com.grablunchtogether.service.favoriteUser;

import com.grablunchtogether.common.exception.UserInfoNotFoundException;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.FavoriteUser;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.favoriteUser.FavoriteUserInput;
import com.grablunchtogether.repository.FavoriteUserRepository;
import com.grablunchtogether.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class FavoriteUserServiceImpl implements FavoriteUserService {
    private final FavoriteUserRepository favoriteUserRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ServiceResult addFavoriteUser(FavoriteUserInput favoriteUserInput,
                                         Long userId,
                                         Long favoriteUserId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserInfoNotFoundException("고객정보를 찾을 수 없습니다."));

        User favoriteUser = userRepository.findById(favoriteUserId)
                .orElseThrow(() -> new UserInfoNotFoundException("고객정보를 찾을 수 없습니다."));

        favoriteUserRepository.save(
                FavoriteUser.builder()
                        .nickName(favoriteUserInput.getNickName())
                        .userId(user)
                        .favoriteUserId(favoriteUser)
                        .registeredAt(LocalDateTime.now())
                        .build()
        );

        return ServiceResult.success("즐겨찾기 유저 추가가 완료되었습니다.");
    }
}
