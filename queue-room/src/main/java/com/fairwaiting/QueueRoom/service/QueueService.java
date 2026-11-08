package com.fairwaiting.QueueRoom.service;

import com.fairwaiting.QueueRoom.kafka.QueueEventProducer;
import com.fairwaiting.QueueRoom.kafka.event.EventType;
import com.fairwaiting.QueueRoom.kafka.event.QueueEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Slf4j
@Service
public class QueueService
{
    private final StringRedisTemplate redisTemplate;  // This is the built-in Spring class that directly talks to the Redis container you just started.
    private final QueueEventProducer eventProducer;

    private static final String QUEUE_KEY = "waiting_room_zset";
    private static final String USER_JOINED_TOPIC = "user-joined";

    public QueueService(StringRedisTemplate redisTemplate, QueueEventProducer eventProducer)
    {
        this.redisTemplate = redisTemplate;
        this.eventProducer = eventProducer;
    }

    public Boolean joinQueue(String userId)
    {
        long timestamp = Instant.now().toEpochMilli();

        Boolean isAdded = redisTemplate.opsForZSet().addIfAbsent(QUEUE_KEY,userId,timestamp);
        boolean isNew = isAdded != null && isAdded;

        if(isNew)
        {
            publishJoinedEvent(userId,timestamp);
        }

        return isNew;
    }

    private void publishJoinedEvent(String userId , long joinedAt)
    {
        QueueEvent event = QueueEvent.builder()
                .eventId("JOINED:"+userId+":"+joinedAt)
                .type(EventType.JOINED)
                .userId(userId)
                .occurredAt(Instant.ofEpochMilli(joinedAt))
                .build();

        try
        {
            eventProducer.publishEvent(USER_JOINED_TOPIC , event);
        }
        catch (Exception e)
        {
            log.error("Could not publish user joined event for {}",userId, e);
        }

    }

    public Long getQueueSize()
    {
        return redisTemplate.opsForZSet().zCard(QUEUE_KEY);
    }

    public Long getUserPosition(String userId)
    {
        return redisTemplate.opsForZSet().rank(QUEUE_KEY , userId);
    }


}
