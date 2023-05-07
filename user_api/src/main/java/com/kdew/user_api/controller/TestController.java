package com.kdew.user_api.controller;

import com.kdew.user_api.service.test.EmailSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final EmailSendService emailSendService;

    @GetMapping
    public void sendTestEmail() {
        String response = emailSendService.sendEmail();
        System.out.println(response);
    }

}
