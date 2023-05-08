package com.kdew.user_api.controller;

import com.kdew.dewdomain.common.UserVo;
import com.kdew.dewdomain.config.JwtAuthenticationProvider;
import com.kdew.user_api.client.domain.customer.ChangeBalanceForm;
import com.kdew.user_api.client.domain.customer.CustomerDto;
import com.kdew.user_api.exception.CustomException;
import com.kdew.user_api.exception.ErrorCode;
import com.kdew.user_api.model.Customer;
import com.kdew.user_api.service.customer.CustomerBalanceService;
import com.kdew.user_api.service.customer.CustomerService;
import io.swagger.annotations.ResponseHeader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final JwtAuthenticationProvider provider;
    private final CustomerService service;
    private final CustomerBalanceService customerBalanceService;

    //고객정보가져오기
    @GetMapping("/getInfo")
    public ResponseEntity<CustomerDto> getInfo(@RequestHeader(name = "X-AUTH-TOKEN") String token) {
        UserVo vo = provider.getUserVo(token);
        Customer c = service.findByIdAndEmail(vo.getId(), vo.getEmail()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );
        return ResponseEntity.ok(CustomerDto.from(c));
    }

    @PostMapping("/balance")
    public ResponseEntity<Integer> changeBalance(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                                 @RequestBody ChangeBalanceForm form) {
        UserVo vo = provider.getUserVo(token);

        return ResponseEntity.ok(customerBalanceService.changeBalance(vo.getId(), form)
                .getCurrentMoney());
    }
}
