package com.grablunchtogether.service;

import com.grablunchtogether.domain.User;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.grablunchtogether.enums.MailComponents.PASSWORD_RESET_SUBJECT;
import static com.grablunchtogether.exception.ErrorCode.USER_INFO_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailSenderService {
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Async
    public void sendEmail(String email, String subject, String text) {
        if (subject.equals(PASSWORD_RESET_SUBJECT)) {
            text = String.format(text, setRandomPassword(findUserByEmail(email)));
        }
        send(email, subject, text);
    }

    private void send(String to, String subject, String text) {
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

    private String setRandomPassword(User user) {
        String randomPassword = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 10);

        String encryptedPassword =
                passwordEncoder.encode(randomPassword);

        user.setPassword(encryptedPassword);

        userRepository.save(user);

        return randomPassword;
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));
    }
}
