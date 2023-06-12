package com.amerikano.cms.user.application;

import com.amerikano.cms.user.domain.SignInForm;
import com.amerikano.cms.user.domain.model.Customer;
import com.amerikano.cms.user.domain.model.Seller;
import com.amerikano.cms.user.exception.CustomException;
import com.amerikano.cms.user.exception.ErrorCode;
import com.amerikano.cms.user.service.customer.CustomerService;
import com.amerikano.cms.user.service.seller.SellerService;
import com.amerikano.domain.config.JwtAuthenticationProvider;
import com.amerikano.domain.domain.common.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignInApplication {

    private final CustomerService customerService;
    private final SellerService sellerService;
    private final JwtAuthenticationProvider provider;

    public String customerLoginToken(SignInForm form) {
        Customer c = customerService.findValidCustomer(form.getEmail(), form.getPassword())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_CHECK_FAIL));

        return provider.createToken(c.getEmail(), c.getId(), UserType.CUSTOMER);
    }

    public String sellerLoginToken(SignInForm form) {
        Seller s = sellerService.findValidSeller(form.getEmail(), form.getPassword())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_CHECK_FAIL));

        return provider.createToken(s.getEmail(), s.getId(), UserType.SELLER);
    }

}
