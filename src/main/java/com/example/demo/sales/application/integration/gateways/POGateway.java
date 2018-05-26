package com.example.demo.sales.application.integration.gateways;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;


@IntegrationComponentScan
@MessagingGateway
@Service
public interface POGateway {
    @Gateway(requestChannel = "builtit-two-http-flow")
    public void sendNotification(String po);

    @Gateway(requestChannel = "builtit-two-mail-flow")
    public void sendNotification(MimeMessage msg);
}

