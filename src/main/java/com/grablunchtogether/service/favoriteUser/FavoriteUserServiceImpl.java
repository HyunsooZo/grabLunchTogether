package com.grablunchtogether.service.favoriteUser;

import com.grablunchtogether.common.exception.AuthorityException;
import com.grablunchtogether.common.exception.ContentNotFoundException;
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
                                         Long otherUserId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserInfoNotFoundException("고객정보를 찾을 수 없습니다."));

        User favoriteUser = userRepository.findById(otherUserId)
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

    @Override
    public ServiceResult editFavoriteUser(FavoriteUserInput favoriteUserEditInput,
                                          Long userId,
                                          Long favoriteUserId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserInfoNotFoundException("고객정보를 찾을 수 없습니다."));

        FavoriteUser favoriteUser = favoriteUserRepository.findById(favoriteUserId)
                .orElseThrow(()-> new ContentNotFoundException("즐겨찾기에 등록되지 않은 유저입니다."));

        if(!favoriteUser.getUserId().equals(user)){
            throw new AuthorityException("본인이 등록한 즐겨찾는 친구정보만 수정할 수 있습니다.");
        }

        favoriteUser.edit(favoriteUserEditInput);

        favoriteUserRepository.save(favoriteUser);

        return ServiceResult.success("즐겨찾기 유저 정보가 수정되었습니다.");
    }

    @Override
    public ServiceResult deleteFavoriteUser(Long userId, Long favoriteUserId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserInfoNotFoundException("고객정보를 찾을 수 없습니다."));

        FavoriteUser favoriteUser = favoriteUserRepository.findById(favoriteUserId)
                .orElseThrow(()-> new ContentNotFoundException("즐겨찾기에 등록되지 않은 유저입니다."));

        if(!favoriteUser.getUserId().equals(user)){
            throw new AuthorityException("본인이 등록한 즐겨찾는 친구정보만 삭제할 수 있습니다.");
        }

        favoriteUserRepository.delete(favoriteUser);

        return ServiceResult.success("즐겨찾는 유저 삭제가 완료되었습니다.");
    }
}
