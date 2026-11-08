package com.fairwaiting.QueueRoom.strategy;

import java.util.List;

public interface AdmissionStrategy
{
    List<String> peekNextUsers(int count);
    // this is the list of users which r about to be removed
    // this helps changing the no of users to be removed

    void removeUsers(List<String> userIds);

}
