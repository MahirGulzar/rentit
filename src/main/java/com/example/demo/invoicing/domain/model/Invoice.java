package com.example.demo.invoicing.domain.model;

import com.example.demo.sales.domain.model.PurchaseOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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

    @OneToOne
    PurchaseOrder purchaseOrder;

    public void setStatus(InvoiceStatus status){
        this.status = status;
    }

}
