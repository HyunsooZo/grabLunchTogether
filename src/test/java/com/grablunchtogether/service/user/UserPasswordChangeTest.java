package com.grablunchtogether.service.user;

import com.grablunchtogether.config.JwtTokenProvider;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.UserDto.PasswordChangeRequest;
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
import static org.mockito.Mockito.when;

@DisplayName("회원 비밀번호변경")
class UserPasswordChangeTest {
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
    @DisplayName("성공")
    void testChangeUserPassword_Success() {
        //given
        User user = User.builder()
                .id(1L)
                .userPassword("1111")
                .userEmail("test@email.com")
                .build();

        PasswordChangeRequest passwordChangeRequest =
                PasswordChangeRequest.builder()
                        .userExistingPassword("1111")
                        .userNewPassword("2222")
                        .build();

        when(jwtTokenProvider.getIdFromToken("token")).thenReturn(user.getId());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("1111","1111"))
                .thenReturn(true);
        when(passwordEncoder.encode("2222")).thenReturn("2222");

        //when
        userService.changeUserPassword(user.getId(), passwordChangeRequest);

        //then
        assertThat(user.getUserPassword()).isEqualTo("2222");
    }

    @Test
    @DisplayName("실패(비밀번호불일치)")
    void testChangeUserPassword_Fail_PasswordNotMatch() {
        //given
        User user = User.builder()
                .id(1L)
                .userPassword(passwordEncoder.encode("1111"))
                .userEmail("test@email.com")
                .build();

        PasswordChangeRequest passwordChangeRequest =
                PasswordChangeRequest.builder()
                        .userExistingPassword("3333")
                        .userNewPassword("2222")
                        .build();

        when(jwtTokenProvider.getIdFromToken("token")).thenReturn(user.getId());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        //when,then
        assertThatThrownBy(() -> userService.changeUserPassword(user.getId(), passwordChangeRequest))
                .isInstanceOf(CustomException.class)
                .hasMessage("아이디 또는 비밀번호가 일치하지않습니다.");
    }

    @Test
    @DisplayName("실패(고객정보없음)")
    void testChangeUserPassword_Fail_UserNotFound() {
        //given
        User user = User.builder()
                .id(1L)
                .userPassword(passwordEncoder.encode("1111"))
                .userEmail("test@email.com")
                .build();

        PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest();

        when(jwtTokenProvider.getIdFromToken("token")).thenReturn(user.getId());
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        //when,then
        assertThatThrownBy(() -> userService.changeUserPassword(user.getId(), passwordChangeRequest))
                .isInstanceOf(CustomException.class)
                .hasMessage("회원정보를 찾을 수 없습니다.");
    }
}