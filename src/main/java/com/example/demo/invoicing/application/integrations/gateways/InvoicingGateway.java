package com.example.demo.invoicing.application.integrations.gateways;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;


@IntegrationComponentScan
@MessagingGateway
@Service
public interface InvoicingGateway {
    @Gateway(requestChannel = "builtit-one-http-flow")
    public void sendInvoice(String invoice);

    @Gateway(requestChannel = "builtit-one-mail-flow")
    public void sendInvoice(MimeMessage msg);
}

