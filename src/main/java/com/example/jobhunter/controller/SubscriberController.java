package com.example.jobhunter.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.jobhunter.entity.Subscriber;
import com.example.jobhunter.service.SubscriberService;
import com.example.jobhunter.util.SecurityUtil;
import com.example.jobhunter.util.annotation.ApiMessage;
import com.example.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class SubscriberController {
    private final SubscriberService subscriberService;

    @PostMapping("subscribers")
    @ApiMessage("Create subscriber")
    public ResponseEntity<Subscriber> createSubscriber(@Valid @RequestBody Subscriber s){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.create(s));
    }

    @PutMapping("subscribers")
    @ApiMessage("Update subscriber")
    public ResponseEntity<Subscriber> updateSubscriber(@RequestBody Subscriber subscriber) {
        Subscriber updatedSubscriber = subscriberService.update(subscriber);
        return ResponseEntity.ok(updatedSubscriber);
    }


    @PostMapping("subscribers/skills")
    @ApiMessage("Get subscriber's skills")
    public ResponseEntity<Subscriber> getSubsSkill(){
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        return ResponseEntity.ok(this.subscriberService.findByEmail(email));
    }
}
