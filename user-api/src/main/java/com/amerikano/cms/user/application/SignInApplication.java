package com.amerikano.cms.user.application;

import com.amerikano.cms.user.domain.SignInForm;
import com.amerikano.cms.user.domain.model.Customer;
import com.amerikano.cms.user.exception.CustomException;
import com.amerikano.cms.user.exception.ErrorCode;
import com.amerikano.cms.user.service.CustomerService;
import com.amerikano.domain.config.JwtAuthenticationProvider;
import com.amerikano.domain.domain.common.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignInApplication {

    private final CustomerService customerService;
    private final JwtAuthenticationProvider provider;

    public String customerLoginToken(SignInForm form) {
        // 1. 로그인 가능 여부
        Customer c = customerService.findValidCustomer(form.getEmail(), form.getPassword())
            .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_CHECK_FAIL));
        // 2. 토큰 발행

        // 3. 토큰을 Response
        return provider.createToken(c.getEmail(), c.getId(), UserType.CUSTOMER);
    }
}
