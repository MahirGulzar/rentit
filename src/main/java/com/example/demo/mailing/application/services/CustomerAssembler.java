package com.example.demo.mailing.application.services;

import com.example.demo.invoicing.rest.InvoiceRestController;
import com.example.demo.mailing.application.dto.CustomerDTO;
import com.example.demo.mailing.domain.model.Customer;
import com.example.demo.mailing.rest.CustomerController;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;


@Service
public class CustomerAssembler extends ResourceAssemblerSupport<Customer, CustomerDTO>{


    public CustomerAssembler() {
        super(CustomerController.class, CustomerDTO.class);
    }

    @Override
    public CustomerDTO toResource(Customer customer) {
        CustomerDTO dto = createResourceWithId(customer.getId(), customer);

        dto.set_id(customer.getId());
        dto.setEmailAddress(customer.getEmailAddress());
        dto.setConsumerURI(customer.getConsumerURI());

        return dto;
    }
//
}
