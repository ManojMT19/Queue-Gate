package com.fairwaiting.QueueRoom.controller;

import com.fairwaiting.QueueRoom.dto.JoinRequest;
import com.fairwaiting.QueueRoom.dto.PositionResponse;
import com.fairwaiting.QueueRoom.service.QueueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/queue")
public class QueueController
{
    private final QueueService queueService;

    public QueueController(QueueService queueService)
    {
        this.queueService = queueService;
    }

    @PostMapping("/join")
    public ResponseEntity<Void> joinQueue(@Valid @RequestBody JoinRequest request)
    {
        Boolean isNew = queueService.joinQueue(request.getUserId());

        if (isNew)
        {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        return ResponseEntity.ok().build(); // 200 will be returned if already present

    }

    @GetMapping("/position/{userId}")
    public ResponseEntity<PositionResponse> postition(@PathVariable String userId)
    {
        Long rank = queueService.getUserPosition(userId);

        if(rank == null)
        {
            return ResponseEntity.notFound().build();
        }
        Long total = queueService.getQueueSize();
        Long displayPosition = rank + 1;
        return ResponseEntity.ok(new PositionResponse(displayPosition , total));
    }


}
