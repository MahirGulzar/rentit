package com.example.demo.invoicing.application.integrations.flows;


import lombok.Data;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.integration.http.dsl.Http;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;


@Component
@ConfigurationProperties
@Configuration
class InvoicingFlow {


//    @Value("${builtItUri.invoice}")
    String invoiceUrl = "http://localhost:8090/api/sales/test_invoices";

    @Configuration
    @PropertySource("classpath:credentials.properties")
    @ConfigurationProperties
    @Data
    class CredentialsProperties {
        private Map<String, Map<String, String>> credentials;
    }

    static class BasicSecureSimpleClientHttpRequestFactory extends SimpleClientHttpRequestFactory {
        @Autowired
        CredentialsProperties credentials;

        public BasicSecureSimpleClientHttpRequestFactory() {}

        public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
            ClientHttpRequest result = super.createRequest(uri, httpMethod);

            for (Map<String, String> map: credentials.getCredentials().values()) {
                String authority = map.get("authority");
                if (authority != null && authority.equals(uri.getAuthority())) {
                    result.getHeaders().add("Authorization", map.get("authorization"));
                    break;
                }
            }
            return result;
        }
    }

    @Bean
    public ClientHttpRequestFactory requestFactory() {
        return new BasicSecureSimpleClientHttpRequestFactory();
    }


    @Bean
    IntegrationFlow BuiltItOneFlow() {
        return IntegrationFlows.from("builtit-one-flow")
                .bridge(null)
                .handle(Http.outboundGateway(invoiceUrl)
                        .httpMethod(HttpMethod.POST).requestFactory(requestFactory())
                )
                .handle("invoiceService", "testmethod")
                .get();
    }
}

