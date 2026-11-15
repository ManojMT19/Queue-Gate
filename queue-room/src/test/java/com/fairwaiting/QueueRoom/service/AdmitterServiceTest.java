
package com.fairwaiting.QueueRoom.service;

import com.fairwaiting.QueueRoom.kafka.QueueEventProducer;
import com.fairwaiting.QueueRoom.strategy.AdmissionStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdmitterServiceTest
{
    @Mock
    private AdmissionStrategy strategy;

    @Mock
    private PassService passService;

    @Mock
    private QueueEventProducer eventProducer;

    @InjectMocks
    private AdmitterService admitterService;

    @Test
    void shouldPublishEventBeforeRemovingUsers()
    {
        // Arrange
        List<String> users = List.of("user-101");

        when(strategy.peekNextUsers(5)).thenReturn(users);
        when(strategy.getUserJoinedAt("user-101")).thenReturn(1000.0);

        // Act
        admitterService.admitUsers(5);

        // Assert: verify the order
        InOrder inOrder = inOrder(
                passService,
                eventProducer,
                strategy
        );

        inOrder.verify(passService).createPass("user-101");

        inOrder.verify(eventProducer).publishEvent(
                eq("user-admitted"),
                any()
        );

        inOrder.verify(strategy).removeUsers(users);
    }

    @Test
    void shouldKeepUserInQueueAndDeletePassWhenPublishingFails()
    {
        // Arrange
        List<String> users = List.of("user-101");

        when(strategy.peekNextUsers(5)).thenReturn(users);
        when(strategy.getUserJoinedAt("user-101")).thenReturn(1000.0);

        doThrow(new IllegalStateException("Kafka publishing failed"))
                .when(eventProducer)
                .publishEvent(eq("user-admitted"), any());

        // Act
        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalStateException.class,
                () -> admitterService.admitUsers(5)
        );

        // Assert
        verify(passService).createPass("user-101");
        verify(passService).deletePass("user-101");
        verify(strategy, never()).removeUsers(anyList());
    }

}
