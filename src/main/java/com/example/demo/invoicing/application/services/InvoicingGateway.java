package com.example.demo.invoicing.application.services;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.http.Http;
import org.springframework.integration.dsl.mail.Mail;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URI;
import java.util.Map;


@IntegrationComponentScan
@MessagingGateway
@Service
public interface InvoicingGateway {
    @Gateway(requestChannel = "sendInvoice-http-channel")
    public void sendInvoice(String invoice);

    @Gateway(requestChannel = "sendInvoice-mail-channel")
    public void sendInvoice(MimeMessage msg);
}

@Component
@ConfigurationProperties
@Configuration
@Service
class InvoicingFlow {

    @Value("${builtItUri.invoice}")
    String invoiceUrl;

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


}