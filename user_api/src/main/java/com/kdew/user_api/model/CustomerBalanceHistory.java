package com.kdew.user_api.model;

import io.swagger.models.auth.In;
import lombok.*;

import javax.persistence.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBalanceHistory extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = Customer.class, fetch = FetchType.LAZY)
    private Customer customer;

    private Integer changeMoney; //변경된 돈
    private Integer currentMoney; // 해당 시점 잔액
    private String fromMessage;
    private String description;
}
