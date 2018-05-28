package com.example.demo.mailing.domain.model;


import com.example.demo.sales.domain.model.PurchaseOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class Customer {

    @Id
    Long id;

    String emailAddress;
    String consumerURI;
}
