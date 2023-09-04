package com.grablunchtogether.service.favoriteUser;

import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.favoriteUser.FavoriteUserInput;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.repository.FavoriteUserRepository;
import com.grablunchtogether.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

class AddFavoriteUserTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private FavoriteUserRepository favoriteUserRepository;

    private FavoriteUserService favoriteUserService;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        favoriteUserService =
                new FavoriteUserService(favoriteUserRepository, userRepository);
    }

    @Test
    public void addFavoriteUser_Success(){
        //given
        User user = User.builder().id(1L).build();
        User favoriteUser = User.builder().id(2L).build();
        FavoriteUserInput favoriteUserInput =
                FavoriteUserInput.builder().nickName("닉네임").build();

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        Mockito.when(userRepository.findById(favoriteUser.getId()))
                .thenReturn(Optional.of(favoriteUser));
        //when
        ServiceResult result = favoriteUserService.addFavoriteUser(favoriteUserInput,
                user.getId(), favoriteUser.getId());
        //then
        Assertions.assertThat(result.isResult()).isTrue();
        Assertions.assertThat(result.getMessage()).isEqualTo("즐겨찾기 유저 추가가 완료되었습니다.");
    }
    @Test
    public void addFavoriteUser_Fail_UserNotFound(){
        //given
        User user = User.builder().id(1L).build();
        User favoriteUser = User.builder().id(2L).build();
        FavoriteUserInput favoriteUserInput =
                FavoriteUserInput.builder().nickName("닉네임").build();

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());
        Mockito.when(userRepository.findById(favoriteUser.getId()))
                .thenReturn(Optional.empty());
        //when,then
        Assertions.assertThatThrownBy(()->favoriteUserService.addFavoriteUser(favoriteUserInput,
                user.getId(), favoriteUser.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("고객정보를 찾을 수 없습니다.");
    }
}