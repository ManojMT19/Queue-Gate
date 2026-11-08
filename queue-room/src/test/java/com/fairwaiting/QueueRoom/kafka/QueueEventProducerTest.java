package com.fairwaiting.QueueRoom.kafka;

import com.fairwaiting.QueueRoom.kafka.event.EventType;
import com.fairwaiting.QueueRoom.kafka.event.QueueEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QueueEventProducerTest
{
    @Mock
    private KafkaTemplate<String,Object> kafkaTemplate;

    @InjectMocks
    private QueueEventProducer producer;

    @Test
    void shouldSendEvenToKafka()
    {
        QueueEvent event = QueueEvent.builder()
                .eventId("TEST:u-101:1000")
                .userId("u-101")
                .type(EventType.ADMITTED)
                .build();

        producer.publishEvent("test-topic",event);

        verify(kafkaTemplate, times(1)).send("test-topic","u-101",event);
    }
}
