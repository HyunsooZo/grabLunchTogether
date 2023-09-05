package com.grablunchtogether.service.favoriteUser;

import com.grablunchtogether.domain.FavoriteUser;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.favoriteUser.FavoriteUserDto;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@DisplayName("즐겨찾는 회원 목록 불러오기")
class ListFavoriteUserTest {
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

        List<FavoriteUserDto.Dto> collect = listEntity.stream()
                .map(FavoriteUserDto.Dto::of)
                .collect(Collectors.toList());

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        Mockito.when(favoriteUserRepository.findByUserId(user))
                .thenReturn(listEntity);
        //when
        List<FavoriteUserDto.Dto> favoriteUsers = favoriteUserService.listFavoriteUser(user.getId());
        //then
        Assertions.assertThat(favoriteUsers).isEqualTo(collect);
    }
}