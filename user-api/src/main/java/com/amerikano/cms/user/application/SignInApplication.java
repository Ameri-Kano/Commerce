package com.amerikano.cms.user.application;

import com.amerikano.cms.user.domain.SignInForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignInApplication {

    public String customerLoginToken(SignInForm form) {
        // 1. 로그인 가능 여부
        // 2. 토큰 발행
        // 3. 토큰을 Response
        return null;
    }
}
