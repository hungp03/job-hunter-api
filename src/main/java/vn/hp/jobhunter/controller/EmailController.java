package vn.hp.jobhunter.controller;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hp.jobhunter.service.EmailService;
import vn.hp.jobhunter.service.SubscriberService;
import vn.hp.jobhunter.util.annotation.ApiMessage;

@RestController
@RequestMapping("api/v1")
public class EmailController {
    private final SubscriberService subscriberService;

    public EmailController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }


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
