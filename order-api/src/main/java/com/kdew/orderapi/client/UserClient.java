package com.kdew.orderapi.client;

import com.kdew.orderapi.client.user.ChangeBalanceForm;
import com.kdew.orderapi.client.user.CustomerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "client", url = "${feign.client.url}")
public interface UserClient {

    @GetMapping("/customer/getInfo")
    ResponseEntity<CustomerDto> getCustomerInfo(@RequestHeader(name = "X-AUTH-TOKEN") String token);

    @PostMapping("/customer/balance")
    ResponseEntity<Integer> chanageBalance(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                           @RequestBody ChangeBalanceForm form);


}