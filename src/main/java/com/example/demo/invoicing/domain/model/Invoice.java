package com.example.demo.invoicing.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class Invoice {
    @Id
    String id;
    Long poID;
    BigDecimal amount;
    LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    InvoiceStatus status;

    public void setStatus(InvoiceStatus status){
        this.status = status;
    }

}
