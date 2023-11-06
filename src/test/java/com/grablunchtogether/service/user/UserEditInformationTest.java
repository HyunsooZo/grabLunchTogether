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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("회원정보 수정")
class UserEditInformationTest {
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
    public void testEditUserInformation_Success() {
        // given
        Long userId = 1L;
        UserDto.InfoEditRequest infoEditRequest =
                UserDto.InfoEditRequest.builder()
                        .userPassword("1234")
                        .userPhoneNumber("123-456-7890")
                        .company("Test Company")
                        .build();

        GeocodeDto coordinate =
                GeocodeDto.builder().latitude(123.456).latitude(789.012).build();

        User existingUser = User.builder()
                .id(userId)
                .email("test@test.com")
                .company("aaa")
                .password("1111")
                .build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("1234")).thenReturn("1111");
        when(passwordEncoder.matches("1234","1111")).thenReturn(true);

        // when
        userService.editUserInformation(userId, infoEditRequest, coordinate);

        // then
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    @DisplayName("실패(비밀번호불일치)")
    public void testEditUserInformation_Fail_InvalidPassword() {
        // given
        Long userId = 1L;
        UserDto.InfoEditRequest infoEditRequest =
                UserDto.InfoEditRequest.builder().userPassword("wrongPassword").build();
        GeocodeDto coordinate = new GeocodeDto();

        User existingUser = new User();
        existingUser.setPassword(passwordEncoder.encode("existingPassword"));
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(existingUser));

        // when, then
        assertThatThrownBy(() -> userService.editUserInformation(userId, infoEditRequest, coordinate))
                .isInstanceOf(CustomException.class)
                .hasMessage("아이디 또는 비밀번호가 일치하지않습니다.");

        verify(userRepository, never()).save(existingUser);
    }

    @Test
    @DisplayName("실패(고객정보 없음)")
    public void testEditUserInformation_Fail_UserInfoNotFound() {
        // given
        Long userId = 1L;
        UserDto.InfoEditRequest infoEditRequest =
                UserDto.InfoEditRequest.builder().userPassword("existingPassword").build();
        GeocodeDto coordinate = new GeocodeDto();

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        // when, then
        assertThatThrownBy(() -> userService.editUserInformation(userId, infoEditRequest, coordinate))
                .isInstanceOf(CustomException.class)
                .hasMessage("회원정보를 찾을 수 없습니다.");

        verify(userRepository, never()).save(any());
    }
}