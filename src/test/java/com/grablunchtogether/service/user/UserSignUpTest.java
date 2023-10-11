package com.grablunchtogether.service.user;

import com.grablunchtogether.config.JwtTokenProvider;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.GeocodeDto;
import com.grablunchtogether.dto.UserDto;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.repository.RefreshTokenRedisRepository;
import com.grablunchtogether.repository.UserOtpRedisRepository;
import com.grablunchtogether.repository.UserRepository;
import com.grablunchtogether.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("회원가입")
class UserSignUpTest {
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
                        refreshTokenRedisRepository
                );
    }

    @Test
    @DisplayName("실패(기존회원)")
    public void testUserSignUp_ExistingUser() {
        // Given
        UserDto.SignUpRequest signUpRequest = UserDto.SignUpRequest.builder()
                .userEmail("existing@example.com")
                .build();

        GeocodeDto userCoordinate = new GeocodeDto();

        User existingUser = new User();
        when(userRepository.findByUserEmail(signUpRequest.getUserEmail()))
                .thenReturn(Optional.of(existingUser));

        // When,Then
        assertThatThrownBy
                (() -> userService.userSignUp(signUpRequest, userCoordinate))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 존재하는 회원입니다.");

        verify(userRepository, times(1))
                .findByUserEmail(signUpRequest.getUserEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("성공")
    void testUserSignUp_Success() throws Exception {
        // Given
        UserDto.SignUpRequest signUpRequest =
                UserDto.SignUpRequest.builder()
                        .userEmail("test@example.com")
                        .userName("Test User")
                        .userPassword("password")
                        .userPhoneNumber("123-4567-1111")
                        .company("Test Company")
                        .build();

        String phoneNumber = signUpRequest.getUserPhoneNumber().replaceAll("-", "");

        GeocodeDto userCoordinate =
                GeocodeDto.builder()
                        .latitude(123.456)
                        .longitude(789.012)
                        .build();

        when(userRepository.findByUserEmail(anyString())).thenReturn(Optional.empty());

        // when
        userService.userSignUp(
                UserDto.SignUpRequest.builder()
                        .userEmail(signUpRequest.getUserEmail())
                        .userName(signUpRequest.getUserName())
                        .userPassword(signUpRequest.getUserPassword())
                        .userPhoneNumber(phoneNumber)
                        .company(signUpRequest.getCompany())
                        .build(), userCoordinate);

        // then
        verify(userRepository).findByUserEmail(eq("test@example.com"));
        verify(userRepository).save(argThat(user ->
                user.getUserEmail().equals("test@example.com") &&
                        user.getUserName().equals("Test User") &&
                        user.getUserPhoneNumber().equals(phoneNumber) &&
                        user.getCompany().equals("Test Company") &&
                        user.getLatitude() == 123.456 &&
                        user.getLongitude() == 789.012
        ));
    }
}