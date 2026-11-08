package com.fairwaiting.QueueRoom.controller;

import com.fairwaiting.QueueRoom.service.PassService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enter")
public class EnterController
{
    private final PassService passService;

    public EnterController(PassService passService)
    {
        this.passService = passService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<String> enter(@PathVariable String userId , @RequestParam String pass)
    {
        boolean isValid = passService.validatePass(userId, pass);

        if(isValid)
        {
            return ResponseEntity.ok("Welcome !!! You successfully entered the event. ");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired pass");
    }


}
