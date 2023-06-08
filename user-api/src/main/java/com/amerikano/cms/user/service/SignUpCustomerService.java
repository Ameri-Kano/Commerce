package com.amerikano.cms.user.service;

import com.amerikano.cms.user.domain.CustomerRepository;
import com.amerikano.cms.user.domain.SignUpForm;
import com.amerikano.cms.user.domain.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpCustomerService {

    private final CustomerRepository customerRepository;

    public Customer signUp(SignUpForm form) {
        return customerRepository.save(Customer.from(form));
    }
}
