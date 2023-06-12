package com.amerikano.cms.order.client.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CustomerDto {

    private Long id;
    private String email;
    private Integer balance;

}
