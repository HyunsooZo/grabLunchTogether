package com.grablunchtogether.service.user;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.user.UserPasswordResetInput;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class MailSenderServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private UserRepository userRepository;

    private MailSenderService mailSenderService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mailSenderService = new MailSenderService(javaMailSender, userRepository);
    }

    @Test
    public void testResetPassword_Success() {
        // given
        String email = "user@example.com";
        UserPasswordResetInput resetInput = new UserPasswordResetInput();
        resetInput.setEmail(email);

        User existingUser = new User();
        when(userRepository.findByUserEmail(email)).thenReturn(java.util.Optional.of(existingUser));

        // when
        ServiceResult result = mailSenderService.resetPassword(resetInput);

        // then
        assertThat(result.isResult()).isTrue();
        assertThat(existingUser.getUserPassword()).isNotNull();
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    public void testResetPassword_Fail_UserInfoNotFound() {
        // given
        String email = "nonexistent@example.com";
        UserPasswordResetInput resetInput = new UserPasswordResetInput();
        resetInput.setEmail(email);

        when(userRepository.findByUserEmail(email)).thenReturn(java.util.Optional.empty());

        // when, then
        assertThatThrownBy(() -> mailSenderService.resetPassword(resetInput))
                .isInstanceOf(CustomException.class)
                .hasMessage("고객정보를 찾을 수 없습니다.");
        verify(javaMailSender, never()).send((MimeMessage) any());
        verify(userRepository, never()).save(any());
    }
}
