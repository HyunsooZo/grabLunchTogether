package com.grablunchtogether.service;

import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.UserDto;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.exception.ErrorCode;
import com.grablunchtogether.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailSenderService {
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void resetPassword(UserDto.PasswordResetRequest passwordResetRequest) {

        User user = userRepository.findByEmail(passwordResetRequest.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_INFO_NOT_FOUND));

        String randomPassword = UUID.randomUUID().toString().substring(0, 10);

        String encryptedPassword =
                passwordEncoder.encode(randomPassword);

        user.setPassword(encryptedPassword);

        sendEmail(
                user.getEmail(),
                "비밀번호 초기화 완료",
                "새로운 비밀번호 : " + randomPassword + "를 입력해 로그인 해주세요."
        );
        userRepository.save(user);
    }

    @Async
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        try {
            javaMailSender.send(message);
            log.info("이메일 전송 성공! 수신자 : " + to);
        } catch (MailException e) {
            log.error("이메일 전송 실패 {}", e.getMessage());
        }
    }
}
