package com.example.demo.common.application.exceptions;

public class CustomerNotFoundException extends Exception {

    public CustomerNotFoundException(Long id){
        super(String.format("Customer not found! (customer ID: %s)", id));
    }
}