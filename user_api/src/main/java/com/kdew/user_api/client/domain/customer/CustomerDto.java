package com.kdew.user_api.client.domain.customer;

import com.kdew.user_api.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class CustomerDto {

    private Long id;
    private String email;
    private Integer balance; // 예치금

    public static CustomerDto from(Customer customer) {
        return new CustomerDto(customer.getId(), customer.getEmail(), customer.getBalance() == null?0: customer.getBalance());
    }
}
