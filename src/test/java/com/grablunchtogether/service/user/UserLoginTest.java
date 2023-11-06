package com.grablunchtogether.service.user;

import com.grablunchtogether.dto.UserDto.*;
import com.grablunchtogether.config.JwtTokenProvider;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.TokenDto;
import com.grablunchtogether.enums.UserRole;
import com.grablunchtogether.enums.UserStatus;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("로그인")
class UserLoginTest {
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
    @DisplayName("실패(비밀번호불일치)")
    public void testLogin_Fail_PasswordNotMatch() {
        // given
        LoginRequest loginRequest =
                new LoginRequest("test@example.com", "1111");

        User existingUser = User.builder()
                .id(2L)
                .name("Test User")
                .email("test@example.com")
                .userStatus(UserStatus.NORMAL)
                .password(passwordEncoder.encode("2222"))
                .build();

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(existingUser));
        when(jwtTokenProvider.issuingAccessToken(any(TokenDto.TokenIssuanceDto.class)))
                .thenReturn("sampleToken");

        // when, then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(CustomException.class)
                .hasMessage("아이디 또는 비밀번호가 일치하지않습니다.");
    }

    @Test
    @DisplayName("실패(이메일불일치)")
    public void testLogin_Fail_EmailNotMatch() {
        // given
        LoginRequest loginRequest =
                new LoginRequest("test@example.com", "1111");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());
        when(jwtTokenProvider.issuingAccessToken(any(TokenDto.TokenIssuanceDto.class)))
                .thenReturn("sampleToken");

        // when, then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(CustomException.class)
                .hasMessage("아이디 또는 비밀번호가 일치하지않습니다.");
    }

    @Test
    @DisplayName("성공")
    public void testLogin_Success() {
        // given
        LoginRequest loginRequest =
                new LoginRequest("test@example.com", "1111");

        User existingUser = User.builder()
                .id(2L)
                .name("Test User")
                .email("test@example.com")
                .password("1111")
                .phoneNumber("00000000000")
                .userRole(UserRole.ROLE_USER)
                .userStatus(UserStatus.NORMAL)
                .latitude(0.0)
                .latitude(0.0)
                .rate(0.0)
                .build();

        when(passwordEncoder.matches("1111","1111"))
                .thenReturn(true);
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(existingUser));
        when(jwtTokenProvider.issuingAccessToken(any(TokenDto.TokenIssuanceDto.class)))
                .thenReturn("sampleToken");
        //when
        TokenDto.Dto login = userService.login(loginRequest);

        //then
        assertThat(login.getUserName()).isEqualTo(existingUser.getName());
    }
}