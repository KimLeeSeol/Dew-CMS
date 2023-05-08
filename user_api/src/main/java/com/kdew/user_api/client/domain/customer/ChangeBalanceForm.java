package com.kdew.user_api.client.domain.customer;

import lombok.Getter;

@Getter
public class ChangeBalanceForm {

    private String from; // 사용자로부터
    private String message;
    private Integer money;
}
