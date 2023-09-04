package com.grablunchtogether.service.user;

import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.configuration.springSecurity.JwtTokenProvider;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.user.UserChangePasswordInput;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.repository.UserRepository;
import com.grablunchtogether.utility.PasswordUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

class UserPasswordChangeTest {
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
    void testChangeUserPassword_Success() {
        //given
        User user = User.builder()
                .id(1L)
                .userPassword(PasswordUtility.getEncryptPassword("1111"))
                .userEmail("test@email.com")
                .build();

        UserChangePasswordInput userChangePasswordInput =
                UserChangePasswordInput.builder()
                        .userExistingPassword("1111")
                        .userNewPassword("2222")
                        .build();

        when(jwtTokenProvider.getIssuer("token")).thenReturn(user.getUserEmail());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        //when
        ServiceResult result =
                userService.changeUserPassword(user.getId(), userChangePasswordInput);

        //then
        assertThat(result).isEqualTo(ServiceResult.success("고객 정보 수정 완료"));
        assertThat(PasswordUtility.isPasswordMatch("2222", user.getUserPassword()));
    }

    @Test
    void testChangeUserPassword_Fail_PasswordNotMatch() {
        //given
        User user = User.builder()
                .id(1L)
                .userPassword(PasswordUtility.getEncryptPassword("1111"))
                .userEmail("test@email.com")
                .build();

        UserChangePasswordInput userChangePasswordInput =
                UserChangePasswordInput.builder()
                        .userExistingPassword("3333")
                        .userNewPassword("2222")
                        .build();

        when(jwtTokenProvider.getIssuer("token")).thenReturn(user.getUserEmail());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        //when,then
        assertThatThrownBy(() -> userService.changeUserPassword(user.getId(), userChangePasswordInput))
                .isInstanceOf(CustomException.class)
                .hasMessage("기존 비밀번호가 일치하지 않습니다.");
    }

    @Test
    void testChangeUserPassword_Fail_UserNotFound() {
        //given
        User user = User.builder()
                .id(1L)
                .userPassword(PasswordUtility.getEncryptPassword("1111"))
                .userEmail("test@email.com")
                .build();

        UserChangePasswordInput userChangePasswordInput = new UserChangePasswordInput();

        when(jwtTokenProvider.getIssuer("token")).thenReturn(user.getUserEmail());
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        //when,then
        assertThatThrownBy(() -> userService.changeUserPassword(user.getId(), userChangePasswordInput))
                .isInstanceOf(CustomException.class)
                .hasMessage("고객 정보를 찾을 수 없습니다. 다시 시도해 주세요.");
    }
}