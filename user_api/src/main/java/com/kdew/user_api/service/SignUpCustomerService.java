package com.kdew.user_api.service;

import com.kdew.user_api.client.domain.SignUpform;
import com.kdew.user_api.client.repository.CustomerRepository;
import com.kdew.user_api.exception.CustomException;
import com.kdew.user_api.exception.ErrorCode;
import com.kdew.user_api.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SignUpCustomerService {

    private final CustomerRepository customerRepository;

    public Customer signUp(SignUpform form) {
        return customerRepository.save(Customer.from(form));
    }

    //email validation
    public boolean isEmailExist(String email) {
        return customerRepository.findByEmail(email.toLowerCase(Locale.ROOT))
                .isPresent();// 이메일 소문자로 바꾸고 존재하는지 확인
    }

    @Transactional
    public void verifyEmail(String email, String code) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (customer.isVerify()) {
            throw new CustomException(ErrorCode.ALREADY_VERIFY);
        }
        else if (!customer.getVerificationCode().equals(code)) {
            throw new CustomException(ErrorCode.WRONG_VERIFICATION);
        }
        else if (customer.getVerifyExpiredAt().isBefore(LocalDateTime.now())) {
            // 만료시간이 현재보다 이전이면
            throw new CustomException(ErrorCode.EXPIRE_CODE);
        }

        customer.setVerify(true);
    }

    @Transactional
    public LocalDateTime ChangeCustomerValidateEmail(Long customerId, String verificationCode) {
        Optional<Customer> customer = customerRepository.findById(customerId);

        if (customer.isPresent()) {
            Customer customerEntity = customer.get();
            customerEntity.setVerificationCode(verificationCode);
            customerEntity.setVerifyExpiredAt(LocalDateTime.now().plusDays(1)); // 1일 뒤에
            return customerEntity.getVerifyExpiredAt();
        }
        throw new CustomException(ErrorCode.NOT_FOUND_USER);
    }
}
