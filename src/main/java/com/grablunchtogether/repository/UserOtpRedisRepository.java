package com.grablunchtogether.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UserOtpRedisRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public void save(String otp, String email) {
        redisTemplate.opsForValue().set(otp, email);
        //3분 뒤 OTP 만료
        redisTemplate.expire(otp, 3, TimeUnit.MINUTES);
    }

    public String check(String otp) {
        return redisTemplate.opsForValue().get(otp);
    }

    public void delete(String otp) {
        redisTemplate.delete(otp);
    }
}
