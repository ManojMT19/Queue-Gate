package com.fairwaiting.logger_service.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "queue_events")
public class QueueEventRecord
{
    @Id
    @Column(nullable = false)
    private String eventId;

    private String userId;

    private Instant occurredAt;

    private String type;

    public QueueEventRecord(){}

    public String getEventId()
    {
        return eventId;
    }

    public void setEventId(String eventId)
    {
        this.eventId = eventId;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public Instant getOccurredAt()
    {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt)
    {
        this.occurredAt = occurredAt;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
