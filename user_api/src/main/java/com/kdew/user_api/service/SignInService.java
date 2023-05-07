package com.kdew.user_api.service;

import com.kdew.dewdomain.common.UserType;
import com.kdew.dewdomain.config.JwtAuthenticationProvider;
import com.kdew.user_api.client.domain.SignInForm;
import com.kdew.user_api.exception.CustomException;
import com.kdew.user_api.exception.ErrorCode;
import com.kdew.user_api.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignInService {

    private final CustomerService customerService;
    private final JwtAuthenticationProvider provider;

    public String customerLoginToken(SignInForm form) {
        //1. 로그인 가능 여부
        Customer c = customerService.findValidCustomer(form.getEmail(), form.getPassword())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_CHECK_FAIL));

        //  토큰 발행
        return provider.createToken(c.getEmail(), c.getId(), UserType.CUSTOMER);
    }
}
