package com.amerikano.cms.order.domain.mailgun;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Data
public class OrderMailForm {
    private String from;
    private String to;
    private String subject;
    private String text;
}
