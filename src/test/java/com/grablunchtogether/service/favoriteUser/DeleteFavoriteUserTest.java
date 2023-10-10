package com.grablunchtogether.service.favoriteUser;

import com.grablunchtogether.domain.FavoriteUser;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.repository.FavoriteUserRepository;
import com.grablunchtogether.repository.UserRepository;
import com.grablunchtogether.service.FavoriteUserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.times;

@DisplayName("즐겨찾는 회원 삭제")
class DeleteFavoriteUserTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private FavoriteUserRepository favoriteUserRepository;

    private FavoriteUserService favoriteUserService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        favoriteUserService =
                new FavoriteUserService(favoriteUserRepository, userRepository);
    }

    @Test
    @DisplayName("성공")
    public void deleteFavoriteUser_Success() {
        //given
        User user = User.builder().id(1L).build();
        User favorite = User.builder().id(2L).build();

        FavoriteUser favoriteUser = FavoriteUser.builder()
                .id(1L)
                .favoriteUserId(favorite)
                .userId(user)
                .nickName("aa")
                .build();

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        Mockito.when(favoriteUserRepository.findById(favoriteUser.getId()))
                .thenReturn(Optional.of(favoriteUser));
        //when
        favoriteUserService.deleteFavoriteUser(user.getId(), favoriteUser.getId());
        //then
        Mockito.verify(favoriteUserRepository, times(1)).delete(favoriteUser);
    }

    @Test
    @DisplayName("실패")
    public void deleteFavoriteUser_Fail() {
        //given
        User user = User.builder().id(4L).build();
        User other = User.builder().id(10L).build();
        User favorite = User.builder().id(2L).build();

        FavoriteUser favoriteUser = FavoriteUser.builder()
                .id(1L)
                .favoriteUserId(favorite)
                .userId(other)
                .nickName("aa")
                .build();

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        Mockito.when(favoriteUserRepository.findById(favoriteUser.getId()))
                .thenReturn(Optional.of(favoriteUser));
        //when
        Assertions.assertThatThrownBy(() -> favoriteUserService.deleteFavoriteUser(user.getId(), favoriteUser.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("해당 데이터에 대한 접근 권한이 없습니다.");
    }
}