package com.grablunchtogether.repository;

import com.grablunchtogether.dto.RefreshTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRedisRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String GRAB_LUNCH = "auth: ";

    public void save(String email, String refreshToken) {
        redisTemplate.opsForValue().set(GRAB_LUNCH + email, refreshToken);
        redisTemplate.expire(GRAB_LUNCH + email, 14 * 24 * 60 * 60, TimeUnit.SECONDS);
    }

    public RefreshTokenDto.Dto findByKey(String email) {
        return RefreshTokenDto.Dto.from(
                GRAB_LUNCH + email, redisTemplate.opsForValue().get(GRAB_LUNCH + email)
        );
    }

    public void deleteByKey(String email) {
        redisTemplate.delete(GRAB_LUNCH + email);
    }
}
