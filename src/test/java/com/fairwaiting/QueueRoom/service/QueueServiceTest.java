package com.fairwaiting.QueueRoom.service;

import com.fairwaiting.QueueRoom.kafka.QueueEventProducer;
import com.fairwaiting.QueueRoom.kafka.event.EventType;
import com.fairwaiting.QueueRoom.kafka.event.QueueEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QueueServiceTest
{
    @Mock private StringRedisTemplate redisTemplate;
    @Mock private QueueEventProducer eventProducer;
    @Mock private ZSetOperations<String , String> zSetOps;

    @InjectMocks private QueueService queueService;

    @BeforeEach
    void setUp()
    {
        when(redisTemplate.opsForZSet()).thenReturn(zSetOps);
    }

    @Test
    void shouldPublishJoinedEventWhenUserisNew()
    {
        when(zSetOps.addIfAbsent(eq("waiting_room_zset"),eq("ravi"),anyDouble())).thenReturn(true);

        Boolean result = queueService.joinQueue("ravi");

//        assertTrue(result);
        assertTrue(result, "joinQueue returned false");

        ArgumentCaptor<QueueEvent> captor = ArgumentCaptor.forClass(QueueEvent.class);
        verify(eventProducer , times(1)).publishEvent(eq("user-joined"),captor.capture());

        QueueEvent sent = captor.getValue();
        assertEquals(EventType.JOINED, sent.getType());
        assertEquals("ravi" , sent.getUserId());
//        assertTrue(sent.getEventId().startsWith("JOINED:ravi:"));
        assertTrue(sent.getEventId().startsWith("JOINED:ravi:"), "eventId was: " + sent.getEventId());
    }

    @Test
    void shouldNotPublishWhenuserAlreadyInQueue()
    {
        when(zSetOps.addIfAbsent(eq("waiting_room_zset"),eq("ravi"),anyDouble())).thenReturn(false);

        Boolean result = queueService.joinQueue("ravi");

        assertFalse(result);
        verify(eventProducer , never()).publishEvent(anyString(), any());
    }
}
