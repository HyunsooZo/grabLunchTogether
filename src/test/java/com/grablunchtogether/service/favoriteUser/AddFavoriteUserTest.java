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
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("즐겨찾는 회원 추가")
class AddFavoriteUserTest {
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
    public void addFavoriteUser_Success() {
        //given
        User user = User.builder().id(1L).build();
        User favoriteUser = User.builder().id(2L).build();
        FavoriteUserDto.Request favoriteUserInput =
                FavoriteUserDto.Request.builder().nickName("닉네임").build();
        FavoriteUser favUser = FavoriteUser.builder()
                .userId(user)
                .favoriteUserId(favoriteUser)
                .nickName(favoriteUserInput.getNickName())
                .build();

        when(userRepository.findById(favUser.getUserId().getId()))
                .thenReturn(Optional.of(favUser.getUserId()));
        when(userRepository.findById(favUser.getFavoriteUserId().getId()))
                .thenReturn(Optional.of(favUser.getFavoriteUserId()));
        //when
        favoriteUserService.addFavoriteUser(
                favoriteUserInput, favUser.getUserId().getId(), favUser.getFavoriteUserId().getId()
        );
        //then
        verify(favoriteUserRepository, times(1)).save(any(FavoriteUser.class));

    }

    @Test
    @DisplayName("실패(회원정보없음)")
    public void addFavoriteUser_Fail_UserNotFound() {
        //given
        User user = User.builder().id(1L).build();
        User favoriteUser = User.builder().id(2L).build();
        FavoriteUserDto.Request favoriteUserInput =
                FavoriteUserDto.Request.builder().nickName("닉네임").build();

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());
        when(userRepository.findById(favoriteUser.getId()))
                .thenReturn(Optional.empty());
        //when,then
        assertThatThrownBy(() -> favoriteUserService.addFavoriteUser(favoriteUserInput,
                user.getId(), favoriteUser.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("회원정보를 찾을 수 없습니다.");
    }
}