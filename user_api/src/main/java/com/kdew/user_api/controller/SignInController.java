package com.kdew.user_api.controller;

import com.kdew.dewdomain.config.JwtAuthenticationProvider;
import com.kdew.user_api.client.domain.SignInForm;
import com.kdew.user_api.service.SignInService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/signIn")
@RequiredArgsConstructor
public class SignInController {
    // 토큰을 발행해서 실제로 접근할 수 있는 근거를 만들어줄것임

    private final SignInService service;
    @PostMapping("/customer")
    public ResponseEntity<String> signInCustomer(@RequestBody SignInForm form) {
        return ResponseEntity.ok(service.customerLoginToken(form));
    }
}
