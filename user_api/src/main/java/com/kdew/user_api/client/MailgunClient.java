package com.kdew.user_api.client;

import com.kdew.user_api.client.mailgun.SendMailForm;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "mailgun", url= "https://api.mailgun.net/v3/")
@Qualifier("mailgun")
public interface MailgunClient {


    @PostMapping("https://api.mailgun.net/v3/sandboxae5945e4709143afa7f4b26e98897582.mailgun.org/messages")
    ResponseEntity<String> sendMail(@SpringQueryMap SendMailForm form); // 메일 보내기
}
