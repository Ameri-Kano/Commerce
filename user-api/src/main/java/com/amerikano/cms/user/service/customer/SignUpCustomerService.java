package com.amerikano.cms.user.service.customer;

import com.amerikano.cms.user.domain.SignUpForm;
import com.amerikano.cms.user.domain.model.Customer;
import com.amerikano.cms.user.domain.repository.CustomerRepository;
import com.amerikano.cms.user.exception.CustomException;
import com.amerikano.cms.user.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignUpCustomerService {

    private final CustomerRepository customerRepository;

    public Customer signUp(SignUpForm form) {
        return customerRepository.save(Customer.from(form));
    }

    public boolean isEmailExist(String email) {
        return customerRepository.findByEmail(email.toLowerCase(Locale.ROOT))
            .isPresent();
    }

    @Transactional
    public void verifyEmail(String email, String code) {
        Customer customer = customerRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (customer.isVerify()) {
            throw new CustomException(ErrorCode.ALREADY_VERIFIED);
        }
        if (!customer.getVerificationCode().equals(code)) {
            throw new CustomException(ErrorCode.WRONG_VERIFICATION);
        }
        if (customer.getVerifyExpiredAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.CODE_EXPIRED);
        }
        customer.setVerify(true);
    }

    @Transactional
    public LocalDateTime changeCustomerValidateEmail(Long customerId, String verificationCode) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            customer.setVerificationCode(verificationCode);
            customer.setVerifyExpiredAt(LocalDateTime.now().plusDays(1));
            return customer.getVerifyExpiredAt();
        }

        throw new CustomException(ErrorCode.NOT_FOUND_USER);
    }
}
