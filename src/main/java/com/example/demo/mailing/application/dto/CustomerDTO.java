package com.example.demo.mailing.application.dto;


import lombok.Data;
import org.springframework.hateoas.ResourceSupport;

@Data
public class CustomerDTO extends ResourceSupport {
    Long _id;
    String emailAddress;
    String consumerURI;
}
