package com.grablunchtogether.service.favoriteUser;

import com.grablunchtogether.common.exception.AuthorityException;
import com.grablunchtogether.common.exception.UserInfoNotFoundException;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.FavoriteUser;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.favoriteUser.FavoriteUserInput;
import com.grablunchtogether.repository.FavoriteUserRepository;
import com.grablunchtogether.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

class editFavoriteUserTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private FavoriteUserRepository favoriteUserRepository;

    private FavoriteUserService favoriteUserService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        favoriteUserService =
                new FavoriteUserServiceImpl(favoriteUserRepository, userRepository);
    }

    @Test
    public void editFavoriteUser_Success() {
        //given
        User user = User.builder().id(1L).build();
        User favorite = User.builder().id(2L).build();

        FavoriteUser favoriteUser = FavoriteUser.builder()
                .id(1L)
                .favoriteUserId(favorite)
                .userId(user)
                .nickName("aa")
                .registeredAt(LocalDateTime.now())
                .build();

        FavoriteUserInput favoriteUserEditInput =
                FavoriteUserInput.builder().nickName("닉네임").build();

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        Mockito.when(favoriteUserRepository.findById(favoriteUser.getId()))
                .thenReturn(Optional.of(favoriteUser));
        //when
        ServiceResult result = favoriteUserService.editFavoriteUser(
                favoriteUserEditInput, user.getId(), favoriteUser.getId()
        );
        //then
        Assertions.assertThat(result.isResult()).isTrue();
        Assertions.assertThat(favoriteUser.getNickName())
                .isEqualTo(favoriteUserEditInput.getNickName());
    }
@Test
    public void editFavoriteUser_Fail_UserNotFound() {
        //given
        User user = User.builder().id(1L).build();
        User favorite = User.builder().id(2L).build();

        FavoriteUser favoriteUser = FavoriteUser.builder()
                .id(1L)
                .favoriteUserId(favorite)
                .userId(user)
                .nickName("aa")
                .registeredAt(LocalDateTime.now())
                .build();

        FavoriteUserInput favoriteUserEditInput =
                FavoriteUserInput.builder().nickName("닉네임").build();

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());
        //when,then
        Assertions.assertThatThrownBy(()->
                favoriteUserService.editFavoriteUser(favoriteUserEditInput,
                        user.getId(), favoriteUser.getId()))
                .isInstanceOf(UserInfoNotFoundException.class)
                .hasMessage("고객정보를 찾을 수 없습니다.");

    }

    @Test
    public void editFavoriteUser_Fail_Authority() {
        //given
        User user = User.builder().id(2L).build();
        User other = User.builder().id(3L).build();
        User favorite = User.builder().id(4L).build();

        FavoriteUser favoriteUser = FavoriteUser.builder()
                .id(1L)
                .favoriteUserId(favorite)
                .userId(other)
                .nickName("aa")
                .registeredAt(LocalDateTime.now())
                .build();

        FavoriteUserInput favoriteUserEditInput =
                FavoriteUserInput.builder().nickName("닉네임").build();

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        Mockito.when(favoriteUserRepository.findById(favoriteUser.getId()))
                .thenReturn(Optional.of(favoriteUser));

        //when,then
        Assertions.assertThatThrownBy(()->
                        favoriteUserService.editFavoriteUser(favoriteUserEditInput,
                                user.getId(), favoriteUser.getId()))
                .isInstanceOf(AuthorityException.class)
                .hasMessage("본인이 등록한 즐겨찾는 친구정보만 수정할 수 있습니다.");
    }
}