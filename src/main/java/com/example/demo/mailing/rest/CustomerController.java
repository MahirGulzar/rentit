package com.example.demo.mailing.rest;


import com.example.demo.common.application.exceptions.CustomerNotFoundException;
import com.example.demo.invoicing.application.dto.InvoiceDTO;
import com.example.demo.invoicing.application.services.InvoiceService;
import com.example.demo.mailing.USER;
import com.example.demo.mailing.application.dto.CustomerDTO;
import com.example.demo.mailing.application.services.MailingService;
import com.example.demo.mailing.domain.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    MailingService mailingService;


    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDTO registerCustomer(
            @RequestParam(name = "emailAddress") String emailAddress,
            @RequestParam(name = "builtITUri") String builtITUri) throws CustomerNotFoundException {
        USER.users.put(builtITUri,emailAddress);

        return mailingService.createCustomer(emailAddress,builtITUri);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<CustomerDTO> getCustomers() {

//        return USER.users.toString();
        return mailingService.getAllCustomers();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerDTO getCustomers(@PathVariable("id") Long id){

        CustomerDTO customerDTO=null;
        try{
            customerDTO = mailingService.getCustomer(id);
        }
        catch (Exception e)
        {
            System.out.println(e);
            return null;
        }
        return customerDTO;
    }
}
