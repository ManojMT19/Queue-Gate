package com.fairwaiting.QueueRoom.strategy;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class FifoStrategy implements AdmissionStrategy
{
    private final StringRedisTemplate redisTemplate;
    private static final String QUEUE_KEY = "waiting_room_zset";


    public FifoStrategy(StringRedisTemplate redisTemplate)
    {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<String> peekNextUsers(int count)
    {
        Set<String> users = redisTemplate.opsForZSet().range(QUEUE_KEY , 0 , count - 1);

        if(users == null)
        {
            return new ArrayList<>();
        }
        return new ArrayList<>(users);
    }

    @Override
    public void removeUsers(List<String> userIds)
    {
        if (userIds.isEmpty())
        {
            return;
        }

        // .remove() needs an array, so we convert our List of strings to an Object array
        redisTemplate.opsForZSet().remove(QUEUE_KEY , userIds.toArray());
    }
}
