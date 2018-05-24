package com.example.demo.invoicing.application.integrations.flows;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.http.dsl.Http;

@Configuration
public class RemittanceFlow {

    @Bean
    IntegrationFlow inBoundRemittanceFlow() {
        return IntegrationFlows.from(
                Http.inboundChannelAdapter("/api/invoicing/remittance")
                        .requestPayloadType(String.class))
                .handle("remittanceService", "processRemittance")
                .get();
    }

}
