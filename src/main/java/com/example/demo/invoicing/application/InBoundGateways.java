package com.example.demo.invoicing.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.mail.dsl.Mail;


@Configuration
public class InBoundGateways {

    @Bean
    IntegrationFlow inboundHttpGateway() {
          return IntegrationFlows.from(
                Http.inboundChannelAdapter("/api/sales/test_invoices")
                        .requestPayloadType(String.class))
                .handle("invoiceService", "processInvoice")
                .get();
    }

    @Value("${gmail.username}")
    String gmailUsername;
    @Value("${gmail.password}")
    String gmailPassword;

    @Bean
    IntegrationFlow inboundMail() {
        return IntegrationFlows.from(Mail.imapInboundAdapter(
                String.format("imaps://%s:%s@imap.gmail.com:993/INBOX", gmailUsername, gmailPassword)
                ).selectorExpression("subject matches '.*invoice.*'"),
                e -> e.autoStartup(true)
                        .poller(Pollers.fixedDelay(40000000))
        ).transform("@invoiceProcessor.extractInvoice(payload)")
                .handle("invoicingService", "processInvoice")
                .get();
    }
}
