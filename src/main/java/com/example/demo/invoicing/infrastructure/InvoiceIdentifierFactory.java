package com.example.demo.invoicing.infrastructure;

import java.util.UUID;


public class InvoiceIdentifierFactory {

    public String nextInvoiceID() {
        return UUID.randomUUID().toString();
    }

}
