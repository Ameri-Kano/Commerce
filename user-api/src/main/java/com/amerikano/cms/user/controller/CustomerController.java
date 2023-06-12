package com.amerikano.cms.user.controller;

import com.amerikano.cms.user.domain.customer.ChangeBalanceForm;
import com.amerikano.cms.user.domain.customer.CustomerDto;
import com.amerikano.cms.user.domain.model.Customer;
import com.amerikano.cms.user.exception.CustomException;
import com.amerikano.cms.user.exception.ErrorCode;
import com.amerikano.cms.user.service.customer.CustomerBalanceService;
import com.amerikano.cms.user.service.customer.CustomerService;
import com.amerikano.domain.config.JwtAuthenticationProvider;
import com.amerikano.domain.domain.common.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final JwtAuthenticationProvider provider;
    private final CustomerService customerService;
    private final CustomerBalanceService customerBalanceService;

    @GetMapping("/getInfo")
    public ResponseEntity<CustomerDto> getInfo(
            @RequestHeader(name = "X-AUTH-TOKEN") String token
    ) {
        UserVo vo = provider.getUserVo(token);
        Customer c = customerService.findByIdAndEmail(vo.getId(), vo.getEmail())
                .orElseThrow(
                        () -> new CustomException(ErrorCode.NOT_FOUND_USER)
                );

        return ResponseEntity.ok(CustomerDto.from(c));
    }

    @PostMapping("/balance")
    public ResponseEntity<Integer> changeBalance(
            @RequestHeader(name = "X-AUTH-TOKEN") String token,
            @RequestBody ChangeBalanceForm form
    ) {
        UserVo vo = provider.getUserVo(token);

        return ResponseEntity.ok(
                customerBalanceService.changeBalance(vo.getId(), form).getCurrentMoney());
    }

}
