package com.example.sns;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    private final SnsService snsService;

    public Controller(SnsService snsService) {
        this.snsService = snsService;
    }

    @PostMapping("/addMessage")
    String addMessage(@RequestParam String body, @RequestParam String subject) {
        return snsService.publishMessage(body, subject);
    }

    @PostMapping("/createTopic")
    String createTopic(@RequestParam String topicName) {
        return snsService.createTopic(topicName);
    }

    @PostMapping("/subscribe")
    String subscribeEmail(@RequestParam String email) {
        snsService.subEmail(email);
        return "Successfully subscribed " + email + ". Please check your email to confirm subscription.";
    }
}
