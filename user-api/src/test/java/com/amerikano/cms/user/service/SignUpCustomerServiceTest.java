package com.amerikano.cms.user.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.amerikano.cms.user.domain.SignUpForm;
import com.amerikano.cms.user.service.customer.SignUpCustomerService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SignUpCustomerServiceTest {

    @Autowired
    private SignUpCustomerService service;

    @Test
    void signUp() {
        SignUpForm form = SignUpForm.builder()
            .name("name")
            .birth(LocalDate.now())
            .email("test@gmail.com")
            .password("1")
            .phone("01012345678")
            .build();

        assertNotNull(service.signUp(form).getId());
    }
}