package com.amerikano.cms.user.service;

import com.amerikano.cms.user.domain.CustomerRepository;
import com.amerikano.cms.user.domain.model.Customer;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Optional<Customer> findValidCustomer(String email, String password) {
        return customerRepository.findByEmail(email).stream()
            .filter(customer -> customer.isVerify() && customer.getPassword().equals(password))
            .findFirst();
    }
}
