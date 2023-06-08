package com.amerikano.cms.user.client.service.test;

import com.amerikano.cms.user.client.MailgunClient;
import com.amerikano.cms.user.client.mailgun.SendMailForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSendService {
    private final MailgunClient mailgunClient;

    public String sendEmail() {
        SendMailForm form = SendMailForm.builder()
                .from("amerikano-test@gmail.com")
                .to("amerikanodevv@gmail.com")
                .subject("mailgun test")
                .text("text test")
                .build();

        return mailgunClient.sendEmail(form).getBody();
    }
}
