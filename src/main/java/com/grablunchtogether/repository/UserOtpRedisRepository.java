package com.grablunchtogether.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UserOtpRedisRepository {
    private final RedisTemplate<String, String> redisTemplate;

    private static final String GRAB_LUNCH = "otp : ";

    public void save(String otp, String phone) {
        redisTemplate.opsForValue().set(GRAB_LUNCH + otp, phone);
        //3분 뒤 OTP 만료
        redisTemplate.expire(otp, 3, TimeUnit.MINUTES);
    }

    public String check(String otp) {
        return redisTemplate.opsForValue().get(GRAB_LUNCH + otp);
    }

    public boolean delete(String otp) {
        return redisTemplate.delete(GRAB_LUNCH + otp);
    }
}
