package com.fairwaiting.QueueRoom.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class PassService
{
    private final StringRedisTemplate redisTemplate;

    private static final String PASS_PREFIX = "pass:";

    public PassService(StringRedisTemplate redisTemplate)
    {
        this.redisTemplate = redisTemplate;
    }

    public String createPass(String userId)
    {
        String token = UUID.randomUUID().toString();
        String key = PASS_PREFIX + userId;

        redisTemplate.opsForValue().set(key , token , Duration.ofMinutes(5));

        return token;
    }

    public boolean validatePass(String userId , String providedToken)
    {
        String key = PASS_PREFIX + userId;
        String storedToken = redisTemplate.opsForValue().get(key);

        System.out.println("DEBUG - Key searched: " + key);
        System.out.println("DEBUG - Token in Redis: [" + storedToken + "]");
        System.out.println("DEBUG - Token from URL: [" + providedToken + "]");

        return storedToken != null && storedToken.equals(providedToken);
    }
}
