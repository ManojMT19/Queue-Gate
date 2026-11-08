package com.fairwaiting.QueueRoom.service;

import com.fairwaiting.QueueRoom.strategy.AdmissionStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdmitterService
{
    private final AdmissionStrategy strategy;
    private final PassService passService;

    public AdmitterService(AdmissionStrategy strategy, PassService passService)
    {
        this.strategy = strategy;
        this.passService = passService;
    }

    public void admitUsers(int count)
    {
        List<String> users = strategy.peekNextUsers(count);

        if(users.isEmpty())
        {
            return;
        }

        for(String user : users)
        {
            passService.createPass(user);
            System.out.println("DEBUG - Admitted user and generated pass for : " + user);
        }

        strategy.removeUsers(users);
    }
}
