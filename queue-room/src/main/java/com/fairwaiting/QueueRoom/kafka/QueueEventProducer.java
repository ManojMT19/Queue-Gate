package com.fairwaiting.QueueRoom.kafka;

import com.fairwaiting.QueueRoom.kafka.event.QueueEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class QueueEventProducer
{
    private final KafkaTemplate<String ,Object> kafkaTemplate;

    public QueueEventProducer(KafkaTemplate<String, Object> kafkaTemplate)
    {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishEvent(String topic , QueueEvent event)
    {
        kafkaTemplate.send(topic , event.getUserId() , event);
        System.out.println("DEBUG  - sent to kafka : "+event.getEventId());
    }

}
