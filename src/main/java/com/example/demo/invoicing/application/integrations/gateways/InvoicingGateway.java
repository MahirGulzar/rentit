package com.example.demo.invoicing.application.integrations.gateways;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;


//@IntegrationComponentScan
@MessagingGateway
@Service
public interface InvoicingGateway {
    @Gateway(requestChannel = "builtit-one-http-channel")
    void sendInvoice(String invoice);

    @Gateway(requestChannel = "builtit-one-mail-channel")
    void sendInvoice(MimeMessage msg);
}

