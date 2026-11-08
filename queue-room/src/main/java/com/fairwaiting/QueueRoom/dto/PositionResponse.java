package com.fairwaiting.QueueRoom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

//@Data
@AllArgsConstructor // this helps in writing new PositionResponse( __ , __ ).
public class PositionResponse
{
    private Long position;
    private Long totalInQueue;

    public Long getPosition()
    {
        return position;
    }

    public void setPosition(Long position)
    {
        this.position = position;
    }

    public Long getTotalInQueue()
    {
        return totalInQueue;
    }

    public void setTotalInQueue(Long totalInQueue)
    {
        this.totalInQueue = totalInQueue;
    }
}
