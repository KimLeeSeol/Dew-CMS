package com.kdew.user_api.service.customer;

import com.kdew.dewdomain.common.UserType;
import com.kdew.dewdomain.config.JwtAuthenticationProvider;
import com.kdew.user_api.client.domain.SignInForm;
import com.kdew.user_api.exception.CustomException;
import com.kdew.user_api.exception.ErrorCode;
import com.kdew.user_api.model.Customer;
import com.kdew.user_api.model.Seller;
import com.kdew.user_api.service.customer.CustomerService;
import com.kdew.user_api.service.seller.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignInService {

    private final CustomerService customerService;
    private final SellerService sellerService;
    private final JwtAuthenticationProvider provider;

    public String customerLoginToken(SignInForm form) {
        //1. 로그인 가능 여부
        Customer c = customerService.findValidCustomer(form.getEmail(), form.getPassword())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_CHECK_FAIL));

        //  토큰 발행
        return provider.createToken(c.getEmail(), c.getId(), UserType.CUSTOMER);
    }

    public String sellerLoginToken(SignInForm form) {

        Seller s = sellerService.findValidSeller(form.getEmail(), form.getPassword())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_CHECK_FAIL));

        return provider.createToken(s.getEmail(), s.getId(), UserType.SELLER);
    }
}
