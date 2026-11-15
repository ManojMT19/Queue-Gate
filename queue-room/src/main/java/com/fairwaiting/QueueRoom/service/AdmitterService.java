package com.fairwaiting.QueueRoom.service;

import com.fairwaiting.QueueRoom.kafka.QueueEventProducer;
import com.fairwaiting.QueueRoom.kafka.event.EventType;
import com.fairwaiting.QueueRoom.kafka.event.QueueEvent;
import com.fairwaiting.QueueRoom.strategy.AdmissionStrategy;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdmitterService
{
    private final AdmissionStrategy strategy;
    private final PassService passService;
    private final QueueEventProducer eventProducer;

    private static final String USER_ADMITTED_TOPIC = "user-admitted";

    public AdmitterService(AdmissionStrategy strategy, PassService passService, QueueEventProducer eventProducer)
    {
        this.strategy = strategy;
        this.passService = passService;
        this.eventProducer = eventProducer;
    }

    public void admitUsers(int count)
    {
        List<String> users = strategy.peekNextUsers(count);

        if(users.isEmpty())
        {
            return;
        }

        List<String> createdPasses = new ArrayList<>();

        // First loop: Create passes for everyone
        for(String user : users)
        {
            passService.createPass(user);
            System.out.println("DEBUG - Admitted user and generated pass for : " + user);

            createdPasses.add(user);
        }
        try
        {
            // Second loop: Publish admission events
            for (String user : users)
            {
                Double score = strategy.getUserJoinedAt(user);

                if (score == null)
                {
                    throw new IllegalStateException("Joining timestamp missing for user: " + user);
                }

                long joinedAt = score.longValue();

                QueueEvent event = QueueEvent.builder()
                        .eventId("ADMITTED:" + user + ":" + joinedAt)
                        .type(EventType.ADMITTED)
                        .userId(user)
                        .occurredAt(Instant.now())
                        .joinedAt(joinedAt)
                        .build();


                eventProducer.publishEvent(USER_ADMITTED_TOPIC, event);
            }
        }
        catch (RuntimeException e)
        {
            for (String temp : createdPasses)
            {
                passService.deletePass(temp);
            }
            throw e;
        }

        // Only after every publish is acknowledged
        strategy.removeUsers(users);
    }
}
