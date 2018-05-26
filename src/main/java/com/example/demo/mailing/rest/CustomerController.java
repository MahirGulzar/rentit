package com.example.demo.mailing.rest;


import com.example.demo.invoicing.application.dto.InvoiceDTO;
import com.example.demo.invoicing.application.services.InvoiceService;
import com.example.demo.mailing.USER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/register_customer")
public class CustomerController {

    @Autowired
    InvoiceService invoiceService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public String registerCustomer(
            @RequestParam(name = "emailAddress") String emailAddress,
            @RequestParam(name = "builtITUri") String builtITUri) {
        USER.users.put(builtITUri,emailAddress);
        return "Your role as domain is Successfully registered! " +
                "\n\nCredentials:\n" +
                "\tUsername: customer\n" +
                "\tPassword: customer";
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public String getCustomers() {
        return USER.users.toString();
    }
}
