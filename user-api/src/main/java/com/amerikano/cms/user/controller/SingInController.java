package com.amerikano.cms.user.controller;

import com.amerikano.cms.user.domain.SignInForm;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/signin")
public class SingInController {

    @PostMapping("/customer")
    public ResponseEntity<String> signInCustomer(@RequestBody SignInForm form) {
        return null;
    }
}
