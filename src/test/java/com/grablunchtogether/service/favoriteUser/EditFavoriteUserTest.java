package com.grablunchtogether.service.favoriteUser;

import com.grablunchtogether.domain.FavoriteUser;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.favoriteUser.FavoriteUserDto;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.repository.FavoriteUserRepository;
import com.grablunchtogether.repository.UserRepository;
import com.grablunchtogether.service.FavoriteUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("즐겨찾는 회원 수정")
class EditFavoriteUserTest {
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
    public void editFavoriteUser_Success() {
        //given
        User user = User.builder().id(1L).build();
        User favorite = User.builder().id(2L).build();

        FavoriteUser favoriteUser = FavoriteUser.builder()
                .id(1L)
                .favoriteUserId(favorite)
                .userId(user)
                .nickName("aa")
                .build();

        FavoriteUserDto.Request favoriteUserEditInput =
                FavoriteUserDto.Request.builder().nickName("닉네임").build();

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        Mockito.when(favoriteUserRepository.findById(favoriteUser.getId()))
                .thenReturn(Optional.of(favoriteUser));
        //when
        favoriteUserService.editFavoriteUser(favoriteUserEditInput, user.getId(), favoriteUser.getId());

        //then
        assertThat(favoriteUser.getNickName()).isEqualTo(favoriteUserEditInput.getNickName());
    }

    @Test
    @DisplayName("실패(회원정보없음)")
    public void editFavoriteUser_Fail_UserNotFound() {
        //given
        User user = User.builder().id(1L).build();
        User favorite = User.builder().id(2L).build();

        FavoriteUser favoriteUser = FavoriteUser.builder()
                .id(1L)
                .favoriteUserId(favorite)
                .userId(user)
                .nickName("aa")
                .build();

        FavoriteUserDto.Request favoriteUserEditInput =
                FavoriteUserDto.Request.builder().nickName("닉네임").build();

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());
        //when,then
        assertThatThrownBy(() ->
                favoriteUserService.editFavoriteUser(favoriteUserEditInput,
                        user.getId(), favoriteUser.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("회원정보를 찾을 수 없습니다.");

    }

    @Test
    @DisplayName("실패(권한없음)")
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
                .build();

        FavoriteUserDto.Request favoriteUserEditInput =
                FavoriteUserDto.Request.builder().nickName("닉네임").build();

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        Mockito.when(favoriteUserRepository.findById(favoriteUser.getId()))
                .thenReturn(Optional.of(favoriteUser));

        //when,then
        assertThatThrownBy(() ->
                favoriteUserService.editFavoriteUser(favoriteUserEditInput,
                        user.getId(), favoriteUser.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("해당 데이터에 대한 접근 권한이 없습니다.");
    }
}