package com.example.demo.common.domain.model;


import lombok.AllArgsConstructor;
import lombok.Value;

import javax.persistence.Embeddable;

@Embeddable
@Value
@AllArgsConstructor(staticName="of")
public class Address {
    private String address;
}
