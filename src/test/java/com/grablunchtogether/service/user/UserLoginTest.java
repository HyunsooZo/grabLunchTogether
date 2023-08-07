package com.grablunchtogether.service.user;

import com.grablunchtogether.common.exception.InvalidLoginException;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.configuration.springSecurity.JwtTokenProvider;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.token.TokenDto;
import com.grablunchtogether.dto.user.UserLoginInput;
import com.grablunchtogether.repository.UserRepository;
import com.grablunchtogether.utility.PasswordUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserLoginTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    private UserServiceImpl userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        userService = new UserServiceImpl(userRepository, jwtTokenProvider);
    }

    @Test
    public void testLogin_Fail_PasswordNotMatch() {
        // given
        UserLoginInput userLoginInput =
                new UserLoginInput("test@example.com", "1111");

        User existingUser = User.builder()
                .userName("Test User")
                .userEmail("test@example.com")
                .userPassword(PasswordUtility.getEncryptPassword("2222"))
                .build();
        existingUser.setId(2L);

        when(userRepository.findByUserEmail("test@example.com"))
                .thenReturn(Optional.of(existingUser));
        when(jwtTokenProvider.issuingToken(any(TokenDto.class)))
                .thenReturn("sampleToken");

        // when, then
        assertThatThrownBy(() -> userService.login(userLoginInput))
                .isInstanceOf(InvalidLoginException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }

    @Test
    public void testLogin_Fail_EmailNotMatch() {
        // given
        UserLoginInput userLoginInput =
                new UserLoginInput("test@example.com", "1111");

        when(userRepository.findByUserEmail("test@example.com"))
                .thenReturn(Optional.empty());
        when(jwtTokenProvider.issuingToken(any(TokenDto.class)))
                .thenReturn("sampleToken");

        // when, then
        assertThatThrownBy(() -> userService.login(userLoginInput))
                .isInstanceOf(InvalidLoginException.class)
                .hasMessage("존재하지 않는 아이디 입니다.");
    }

    @Test
    public void testLogin_Success() {
        // given
        UserLoginInput userLoginInput =
                new UserLoginInput("test@example.com", "1111");

        User existingUser = User.builder()
                .userName("Test User")
                .userEmail("test@example.com")
                .userPassword(PasswordUtility.getEncryptPassword("1111"))
                .build();
        existingUser.setId(2L);

        when(userRepository.findByUserEmail("test@example.com"))
                .thenReturn(Optional.of(existingUser));
        when(jwtTokenProvider.issuingToken(any(TokenDto.class)))
                .thenReturn("sampleToken");
        //when
        ServiceResult result = userService.login(userLoginInput);

        //then
        assertThat(result).isEqualTo(ServiceResult.success("sampleToken"));
    }
}