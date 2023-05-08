package com.kdew.user_api.model;

import com.kdew.user_api.client.domain.SignUpform;
import lombok.*;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class) // Customer이 변경이 될때마다 자동으로 create데이터와 업데이트 데이터가 변경이 됨
public class Customer extends BaseEntity{

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;
    private String name;
    private String password;
    private String phone;
    private LocalDate birth;

    // 이메일 유효성 검사
    private LocalDateTime verifyExpiredAt; // 이메일을 무한정 받을 수 없으니까
    private String verificationCode; // 코드 기반으로 인증 하니까
    private boolean verify;

    @Column(columnDefinition = "int default 0")
    private Integer balance; // 예치금

    public static Customer from(SignUpform form) {

        return Customer.builder()
                .email(form.getEmail().toLowerCase(Locale.ROOT)) // 유니크 체크를 할거라서 toLowerCase
                .password(form.getPassword())
                .name(form.getName())
                .birth(form.getBirth())
                .phone(form.getPhone())
                .verify(false)
                .build();
    }
}
