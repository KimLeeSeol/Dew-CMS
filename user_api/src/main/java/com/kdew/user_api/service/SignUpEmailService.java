package com.kdew.user_api.service;

import com.kdew.user_api.client.MailgunClient;
import com.kdew.user_api.client.domain.SignUpform;
import com.kdew.user_api.client.mailgun.SendMailForm;
import com.kdew.user_api.exception.CustomException;
import com.kdew.user_api.exception.ErrorCode;
import com.kdew.user_api.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.RandomStringUtils;

@Service
@RequiredArgsConstructor
public class SignUpEmailService {

    private final MailgunClient mailgunClient;
    private final SignUpCustomerService signUpCustomerService;

    public void customerVerify(String email, String code){
        signUpCustomerService.verifyEmail(email,code);
    }
    public String customerSignUp(SignUpform form) {
        if(signUpCustomerService.isEmailExist(form.getEmail())) {
            throw new CustomException(ErrorCode.ALREADY_REGISTER_USER);
        }
        else {
            Customer c = signUpCustomerService.signUp(form); // 회원가입 완료하면!

            String code = getRandomCode();
            SendMailForm sendMailForm = SendMailForm.builder()
                            .from("tester@gemail.com")
                            .to(form.getEmail())
                            .subject("Verification Email")
                            .text(getVerificationEmailBody(c.getEmail(), c.getName(), code))
                            .build();
            mailgunClient.sendMail(sendMailForm);

            signUpCustomerService.ChangeCustomerValidateEmail(c.getId(), code);
            return "회원 가입에 성공하였습니다.";

        }
    }

    private String getRandomCode() {
        return RandomStringUtils.random(10,true,true);
    }

    // 템플릿 만들기
    private String getVerificationEmailBody(String email, String name, String code) {
        StringBuilder builder = new StringBuilder();
        return builder.append("Hello ").append(name).append("! please Click Link for verification.\n\n")
                .append("http://localhost:8082/customer/verify/customer?email=")
                .append(email)
                .append("&code=")
                .append(code).toString()
                ;
    }


}
