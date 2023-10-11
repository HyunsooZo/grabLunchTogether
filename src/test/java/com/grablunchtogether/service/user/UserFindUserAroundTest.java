package com.grablunchtogether.service.user;

import com.grablunchtogether.config.JwtTokenProvider;
import com.grablunchtogether.dto.UserDistanceDto;
import com.grablunchtogether.repository.RefreshTokenRedisRepository;
import com.grablunchtogether.repository.UserOtpRedisRepository;
import com.grablunchtogether.repository.UserRepository;
import com.grablunchtogether.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@DisplayName("주변회원 찾기")
class UserFindUserAroundTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private UserOtpRedisRepository userOtpRedisRepository;
    @Mock
    private RefreshTokenRedisRepository refreshTokenRedisRepository;

    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        userService =
                new UserService(
                        userRepository,
                        jwtTokenProvider,
                        passwordEncoder,
                        userOtpRedisRepository,
                        refreshTokenRedisRepository);
    }

    @Test
    @DisplayName("성공")
    public void TestFindUserAround_Success() {
        //given
        double latitude = 37.12345;
        double longitude = 127.98765;
        double kilometer = 10.0;

        List<UserDistanceDto.Dto> list = new ArrayList<>();
        list.add(UserDistanceDto.Dto.builder()
                .id(String.valueOf(3))
                .userName("테스트")
                .userEmail("test@test.com")
                .userRate(0.0)
                .company("테스트회사")
                .distance(String.format("%.1f", 0.1234))
                .build());

        List<Object[]> listFromRepo = new ArrayList<>();
        listFromRepo.add(new Object[]{"test@test.com","테스트",  0.0, "테스트회사",3, 0.1234});

        when(userRepository.getUserListByDistance(latitude, longitude, kilometer))
                .thenReturn(listFromRepo);
        //when
        List<UserDistanceDto.Dto> userAround = userService.findUserAround(latitude, longitude, kilometer);
        //then
        Assertions.assertThat(userAround.get(0).getId()).isEqualTo(list.get(0).getId());
    }


    @Test
    @DisplayName("성공(빈리스트)")
    public void TestFindUserAround_Fail() {
        //given
        double latitude = 37.12345;
        double longitude = 127.98765;
        double kilometer = 10.0;

        List<UserDistanceDto.Dto> list = new ArrayList<>();

        List<Object[]> listFromRepo = new ArrayList<>();
        listFromRepo.add(new Object[]{"테스트", "test@test.com", 0.0, "테스트회사", 0.1234});

        when(userRepository.getUserListByDistance(latitude, longitude, kilometer))
                .thenReturn(Collections.emptyList());
        //when
        List<UserDistanceDto.Dto> userAround = userService.findUserAround(latitude, longitude, kilometer);
        //then
        Assertions.assertThat(userAround).isEqualTo(list);
    }
}