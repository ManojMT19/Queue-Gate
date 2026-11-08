package com.fairwaiting.QueueRoom.kafka.event;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class QueueEvent
{
    private String eventId;
    private EventType type;
    private String userId;
    private Instant occurredAt;
    private Long joinedAt;  // this is used when type is admitted
}
