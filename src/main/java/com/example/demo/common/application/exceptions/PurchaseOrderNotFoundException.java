package com.example.demo.common.application.exceptions;


public class PurchaseOrderNotFoundException extends Exception {

    public PurchaseOrderNotFoundException(Long id){
        super(String.format("Purchase order not found! (Order ID: %s)", id));
    }
}
