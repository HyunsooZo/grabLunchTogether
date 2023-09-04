package com.grablunchtogether.service.user;

import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.configuration.springSecurity.JwtTokenProvider;
import com.grablunchtogether.dto.user.UserDistanceDto;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

class UserFindUserAroundTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        userService = new UserService(userRepository, jwtTokenProvider);
    }

    @Test
    public void TestFindUserAround_Success() {
        //given
        double latitude = 37.12345;
        double longitude = 127.98765;
        double kilometer = 10.0;

        List<UserDistanceDto> list = new ArrayList<>();
        list.add(UserDistanceDto.builder()
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
        ServiceResult result =
                userService.findUserAround(latitude, longitude, kilometer);
        //then
        assertThat(result.isResult()).isTrue();
        assertThat((List<Object[]>) result.getObject()).isEqualTo(list);
    }


    @Test
    public void TestFindUserAround_Fail() {
        //given
        double latitude = 37.12345;
        double longitude = 127.98765;
        double kilometer = 10.0;

        List<UserDistanceDto> list = new ArrayList<>();
        list.add(UserDistanceDto.builder()
                .userName("테스트")
                .userEmail("test@test.com")
                .userRate(0.0)
                .company("테스트회사")
                .distance(String.format("%.1f", 0.1234))
                .build());

        List<Object[]> listFromRepo = new ArrayList<>();
        listFromRepo.add(new Object[]{"테스트", "test@test.com", 0.0, "테스트회사", 0.1234});

        when(userRepository.getUserListByDistance(latitude, longitude, kilometer))
                .thenReturn(Collections.emptyList());
        //when,then
        assertThatThrownBy(() -> userService.findUserAround(latitude, longitude, kilometer))
                .isInstanceOf(CustomException.class)
                .hasMessage("해당 조건으로 조회되는 주변회원이 없습니다.");
    }
}