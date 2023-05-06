package com.kdew.user_api.controller;

import com.kdew.user_api.client.domain.SignUpform;
import com.kdew.user_api.service.SignUpEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/signup")
@RequiredArgsConstructor
public class SignUpController {

    private final SignUpEmailService service;


    @PostMapping
    public ResponseEntity<String> customerSignUp(@RequestBody SignUpform form) {
        return ResponseEntity.ok(service.customerSignUp(form));
    }

    @PutMapping("/verify/customer")
    public ResponseEntity<String> verifyCustomer(String email, String code) {
        service.customerVerify(email,code);
        return ResponseEntity.ok("인증이 완료되었습니다.");
    }
}
