package com.grablunchtogether.service.favoriteUser;

import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.FavoriteUser;
import com.grablunchtogether.domain.User;
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

import static org.mockito.Mockito.times;

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
        ServiceResult result =
                favoriteUserService.deleteFavoriteUser(user.getId(), favoriteUser.getId()
                );
        //then
        Assertions.assertThat(result.isResult()).isTrue();
        Mockito.verify(favoriteUserRepository, times(1)).delete(favoriteUser);
    }

    @Test
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
                .hasMessage("본인이 등록한 즐겨찾는 친구정보만 삭제할 수 있습니다.");
    }
}