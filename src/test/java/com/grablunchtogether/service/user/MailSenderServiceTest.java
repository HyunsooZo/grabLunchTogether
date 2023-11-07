package com.grablunchtogether.service.user;

import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.UserDto;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.repository.UserRepository;
import com.grablunchtogether.service.MailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.mail.internet.MimeMessage;

import static com.grablunchtogether.enums.MailComponents.PASSWORD_RESET_SUBJECT;
import static com.grablunchtogether.enums.MailComponents.PASSWORD_RESET_TEXT;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("메일전송(비밀번호 초기화)")
class MailSenderServiceTest {

    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;

    private MailSenderService mailSenderService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mailSenderService = new MailSenderService(javaMailSender, userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("성공")
    public void testResetPassword_Success() {
        // given
        String email = "user@example.com";
        UserDto.PasswordResetRequest resetInput = new UserDto.PasswordResetRequest(email);

        User existingUser = new User();
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(existingUser));

        // when
        mailSenderService.sendEmail(resetInput.getEmail(), PASSWORD_RESET_SUBJECT, PASSWORD_RESET_TEXT);

        // then
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    @DisplayName("실패(사용자정보없음)")
    public void testResetPassword_Fail_UserInfoNotFound() {
        // given
        String email = "nonexistent@example.com";
        UserDto.PasswordResetRequest resetInput = new UserDto.PasswordResetRequest(email);

        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.empty());

        // when, then
        assertThatThrownBy(() -> mailSenderService.sendEmail(
                resetInput.getEmail(),
                PASSWORD_RESET_SUBJECT,
                PASSWORD_RESET_TEXT)
        )
                .isInstanceOf(CustomException.class)
                .hasMessage("회원정보를 찾을 수 없습니다.");
        verify(javaMailSender, never()).send((MimeMessage) any());
        verify(userRepository, never()).save(any());
    }
}
