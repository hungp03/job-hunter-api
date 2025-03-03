package vn.hp.jobhunter.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hp.jobhunter.entity.Subscriber;
import vn.hp.jobhunter.service.SubscriberService;
import vn.hp.jobhunter.util.SecurityUtil;
import vn.hp.jobhunter.util.annotation.ApiMessage;
import vn.hp.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("api/v1")
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("subscribers")
    @ApiMessage("Create subscriber")
    public ResponseEntity<Subscriber> createSubscriber(@Valid @RequestBody Subscriber s) throws IdInvalidException {
        if (this.subscriberService.isEmailExisted(s.getEmail())){
            throw new IdInvalidException("Email đã được đăng ký!");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.create(s));
    }

    @PutMapping("subscribers")
    public ResponseEntity<Subscriber> updateSubscriber(@RequestBody Subscriber s) throws IdInvalidException{
        Subscriber subsDB = this.subscriberService.getSubscriber(s.getId());
        if (subsDB == null){
            throw new IdInvalidException("Subscriber id = "+ s.getId() + " không tồn tại");
        }
        return ResponseEntity.ok(this.subscriberService.update(subsDB, s));

    }

    @PostMapping("subscribers/skills")
    @ApiMessage("Get subscriber's skills")
    public ResponseEntity<Subscriber> getSubsSkill(){
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        return ResponseEntity.ok(this.subscriberService.findByEmail(email));
    }
}
