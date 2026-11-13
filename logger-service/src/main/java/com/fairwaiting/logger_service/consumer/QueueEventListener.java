package com.fairwaiting.logger_service.consumer;

import com.fairwaiting.logger_service.entity.QueueEventRecord;
import com.fairwaiting.logger_service.repository.QueueEventRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class QueueEventListener
{
    private static final Logger log = LoggerFactory.getLogger(QueueEventListener.class);
    private final QueueEventRecordRepository repository;
    private final ObjectMapper objectMapper;

    public QueueEventListener(
            QueueEventRecordRepository repository,
            ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "user-joined")
    public void onMessage(String message)
    {
        try{
            QueueEventRecord event = objectMapper.readValue(message , QueueEventRecord.class);

            if(repository.existsById(event.getEventId()))
            {
                log.info("Duplicate event ignored: {}", event.getEventId());
                return;
            }

            repository.save(event);

            log.info("Event saved successfully : {}",event.getEventId());
        }
        catch(Exception e)
        {
            log.error("Failed to process kafka event : {}",message,e);
        }
    }
}
