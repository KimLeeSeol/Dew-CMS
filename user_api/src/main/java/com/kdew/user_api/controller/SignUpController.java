package com.kdew.user_api.controller;

import com.kdew.user_api.client.domain.SignUpform;
import com.kdew.user_api.service.customer.SignUpEmailService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/signup", produces="text/plain;charset=UTF-8")
@RequiredArgsConstructor
public class SignUpController {

    private final SignUpEmailService service;


    @PostMapping("/customer")
    public ResponseEntity<String> customerSignUp(@RequestBody SignUpform form) {
        return ResponseEntity.ok(service.customerSignUp(form));
    }

    @GetMapping("/customer/verify")
    public ResponseEntity<String> verifyCustomer(String email, String code) {
        service.customerVerify(email,code);
        return ResponseEntity.ok("인증이 완료되었습니다.");
    }

    @PostMapping( "/seller")
    public ResponseEntity<String> sellerSignUp(@RequestBody SignUpform form) {
        return ResponseEntity.ok(service.sellerSignUp(form));
    }

    @GetMapping( "/seller/verify")
    public ResponseEntity<String> verifySeller(String email, String code) {
        service.sellerVerify(email, code);
        return ResponseEntity.ok("인증이 완료되었습니다.");
    }
}
