package com.amerikano.cms.order.client;

import com.amerikano.cms.order.domain.mailgun.OrderMailForm;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "mailgun", url = "https://api.mailgun.net/v3/")
@Qualifier("mailgun")
public interface OrderMailClient {
    @PostMapping("sandbox9d5bf49757db43f1b2f8f66df64b54ac.mailgun.org/messages")
    ResponseEntity<String> sendEmail(@SpringQueryMap OrderMailForm form);
}
