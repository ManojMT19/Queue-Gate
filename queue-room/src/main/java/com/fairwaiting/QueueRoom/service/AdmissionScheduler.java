package com.fairwaiting.QueueRoom.service;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class AdmissionScheduler
{
    private final  AdmitterService admitterService;

    public AdmissionScheduler(AdmitterService admitterService)
    {
        this.admitterService = admitterService;
    }

    @Scheduled(fixedDelay = 10000) // 10sec
    public void processQueue()
    {
        admitterService.admitUsers(5);
    }
}
