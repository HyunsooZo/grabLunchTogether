package com.grablunchtogether.service;


import com.grablunchtogether.config.JwtTokenProvider;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.TokenDto;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.repository.RefreshTokenRedisRepository;
import com.grablunchtogether.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.grablunchtogether.dto.RefreshTokenDto.AccessToken;
import static com.grablunchtogether.dto.RefreshTokenDto.Dto;
import static com.grablunchtogether.exception.ErrorCode.REFRESH_CODE_EXPIRED;
import static com.grablunchtogether.exception.ErrorCode.USER_INFO_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 신규 AccessToken 발급
    public AccessToken issueNewAccessToken(String accessToken, String refreshToken) {


        validateRefreshToken(refreshToken);
        String userEmail = jwtTokenProvider.getEmailFromToken(refreshToken);
        User targetUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        Dto tokenSubject =
                refreshTokenRedisRepository.findByKey(targetUser.getEmail());

        if (tokenSubject == null) {
            throw new CustomException(REFRESH_CODE_EXPIRED);
        }

        User user = findUser(targetUser.getEmail());

        String newAccessToken = jwtTokenProvider.issuingAccessToken(
                TokenDto.TokenIssuanceDto.from(user)
        );
        return AccessToken.builder()
                .accessToken(newAccessToken)
                .build();
    }

    // 리프레시 토큰 삭제
    public void deleteRefreshToken(String email) {
        refreshTokenRedisRepository.deleteByKey(email);
    }

    // RefreshToken 유효성 검사
    private void validateRefreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(REFRESH_CODE_EXPIRED);
        }
    }

    // User 찾기
    private User findUser(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));
    }
}
