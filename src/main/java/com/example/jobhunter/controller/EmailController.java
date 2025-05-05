package com.example.jobhunter.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.jobhunter.service.SubscriberService;
import com.example.jobhunter.util.annotation.ApiMessage;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class EmailController {
    private final SubscriberService subscriberService;

    @GetMapping("email")
    @ApiMessage("Send simple email")
//    @Scheduled(cron = "*/30 * * * * *")
//    @Transactional
    public String sendSimpleEmail(){
        //System.out.println("Test email");
        this.subscriberService.sendSubscribersEmailJobs();
        return "Ok!";
    }
}
