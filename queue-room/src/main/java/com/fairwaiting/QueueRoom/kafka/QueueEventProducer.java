package com.fairwaiting.QueueRoom.kafka;

import com.fairwaiting.QueueRoom.kafka.event.QueueEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class QueueEventProducer
{
    private final KafkaTemplate<String ,Object> kafkaTemplate;

    public QueueEventProducer(KafkaTemplate<String, Object> kafkaTemplate)
    {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishEvent(String topic, QueueEvent event)
    {
        try
        {
            kafkaTemplate.send(topic, event.getUserId(), event)
                    .get(5, TimeUnit.SECONDS);

            log.info("Successfully published event: {}", event.getEventId());
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Kafka publishing was interrupted", e);
        }
        catch (ExecutionException | TimeoutException e)
        {
            throw new IllegalStateException("Failed to publish event to Kafka", e);
        }
    }

}
