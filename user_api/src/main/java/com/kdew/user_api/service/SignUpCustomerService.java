package com.kdew.user_api.service;

import com.kdew.user_api.client.domain.SignUpform;
import com.kdew.user_api.client.repository.CustomerRepository;
import com.kdew.user_api.model.CustomerEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpCustomerService {

    private final CustomerRepository customerRepository;

    public CustomerEntity signUp(SignUpform form) {
        return customerRepository.save(CustomerEntity.from(form));
    }
}
