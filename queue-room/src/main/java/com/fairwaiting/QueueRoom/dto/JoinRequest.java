package com.fairwaiting.QueueRoom.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

//@Data // Lombok gives us getters/setters automatically
public class JoinRequest
{
    @NotBlank(message =  "userId cannot be blank")
    private String userId;

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }
}
