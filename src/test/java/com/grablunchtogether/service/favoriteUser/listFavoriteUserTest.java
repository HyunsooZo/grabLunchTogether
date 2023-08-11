package com.grablunchtogether.service.favoriteUser;

import com.grablunchtogether.common.exception.ContentNotFoundException;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.FavoriteUser;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.favoriteUser.FavoriteUserDto;
import com.grablunchtogether.repository.FavoriteUserRepository;
import com.grablunchtogether.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class listFavoriteUserTest {
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
    public void listFavoriteUser_Success() {
        //given
        User user = User.builder().id(1L).build();
        User favorite = User.builder().id(2L).build();

        FavoriteUser favoriteUser1 = FavoriteUser.builder()
                .id(1L)
                .favoriteUserId(favorite)
                .userId(user)
                .nickName("aa")
                .build();

        FavoriteUser favoriteUser2 = FavoriteUser.builder()
                .id(2L)
                .favoriteUserId(favorite)
                .userId(user)
                .nickName("aa")
                .build();

        List<FavoriteUser> listEntity = new ArrayList<>();
        listEntity.add(favoriteUser1);
        listEntity.add(favoriteUser2);

        List<FavoriteUserDto> listDto = new ArrayList<>();
        listDto.add(FavoriteUserDto.of(favoriteUser1));
        listDto.add(FavoriteUserDto.of(favoriteUser2));

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        Mockito.when(favoriteUserRepository.findByUserId(user))
                .thenReturn(listEntity);
        //when
        ServiceResult result =
                favoriteUserService.listFavoriteUser(user.getId());
        //then
        Assertions.assertThat(result.isResult()).isTrue();
        Assertions.assertThat(result.getObject()).isEqualTo(listDto);
    }

    @Test
    public void listFavoriteUser_Fail_ListEmpty() {
        //given
        User user = User.builder().id(1L).build();
        User favorite = User.builder().id(2L).build();

        FavoriteUser favoriteUser1 = FavoriteUser.builder()
                .id(1L)
                .favoriteUserId(favorite)
                .userId(user)
                .nickName("aa")
                .build();

        FavoriteUser favoriteUser2 = FavoriteUser.builder()
                .id(2L)
                .favoriteUserId(favorite)
                .userId(user)
                .nickName("aa")
                .build();

        List<FavoriteUser> listEntity = new ArrayList<>();

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        Mockito.when(favoriteUserRepository.findByUserId(user))
                .thenReturn(listEntity);
        //when,then
        Assertions.assertThatThrownBy(() -> favoriteUserService.listFavoriteUser(user.getId()))
                .isInstanceOf(ContentNotFoundException.class)
                .hasMessage("등록된 즐겨찾는 유저가 존재하지 않습니다.");
    }
}