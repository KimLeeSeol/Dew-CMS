package com.kdew.user_api.model;

import com.fasterxml.jackson.databind.ser.Serializers;
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
@AuditOverride(forClass = BaseEntity.class)
public class Seller extends BaseEntity {
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

    public static Seller from(SignUpform form) {

        return Seller.builder()
                .email(form.getEmail().toLowerCase(Locale.ROOT)) // 유니크 체크를 할거라서 toLowerCase
                .password(form.getPassword())
                .name(form.getName())
                .birth(form.getBirth())
                .phone(form.getPhone())
                .verify(false)
                .build();
    }
}
