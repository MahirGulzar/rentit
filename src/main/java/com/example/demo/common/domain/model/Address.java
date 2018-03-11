package com.example.demo.common.domain.model;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.persistence.Embeddable;
import java.util.regex.Pattern;

@Embeddable
//@Value
//@NoArgsConstructor(force=true,access= AccessLevel.PRIVATE)
//@AllArgsConstructor(staticName="of")
public class Address {
    private String address;

    public static Address of(String anAddress) {
        Address newAddress = new Address();
        newAddress.setAddress(anAddress);
        return newAddress;
    }
    private void setAddress(String anAddress) {
        if (anAddress == null)
            throw new IllegalArgumentException("The email address is required.");
        if (Pattern.matches("\\w+([-+.']\\w+)*@\\w+ . . .*", anAddress))
            throw new IllegalArgumentException("Email address format is invalid.");

        this.address = anAddress;
    }
}
