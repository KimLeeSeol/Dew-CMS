package com.kdew.user_api.service.test;

import com.kdew.user_api.client.MailgunClient;
import com.kdew.user_api.client.mailgun.SendMailForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSendService {

    private final MailgunClient mailgunClient;

    public String sendEmail() {
        SendMailForm form = SendMailForm.builder()
                .from("h2ju1004@gmail.com")
                .to("h2ju1004@gmail.com")
                .subject("Test email from zero base")
                .text("my text")
                .build();
        return mailgunClient.sendMail(form).getBody();
    }

}