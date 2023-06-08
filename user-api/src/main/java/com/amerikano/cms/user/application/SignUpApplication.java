package com.amerikano.cms.user.application;

import com.amerikano.cms.user.client.MailgunClient;
import com.amerikano.cms.user.domain.SignUpForm;
import com.amerikano.cms.user.domain.model.Customer;
import com.amerikano.cms.user.exception.CustomException;
import com.amerikano.cms.user.exception.ErrorCode;
import com.amerikano.cms.user.service.SignUpCustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpApplication {
    private final MailgunClient mailgunClient;
    private final SignUpCustomerService signUpCustomerService;

    public void customerSignUp(SignUpForm form) {
        if(signUpCustomerService.isEmailExist(form.getEmail())) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_USER);
        } else {
            Customer c = signUpCustomerService.signUp(form);
        }
    }


}
