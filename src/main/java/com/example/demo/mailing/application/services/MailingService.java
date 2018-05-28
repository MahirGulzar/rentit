package com.example.demo.mailing.application.services;


import com.example.demo.common.application.exceptions.CustomerNotFoundException;
import com.example.demo.mailing.application.dto.CustomerDTO;
import com.example.demo.mailing.application.factory.CustomerIdentifierFactory;
import com.example.demo.mailing.domain.model.Customer;
import com.example.demo.mailing.domain.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MailingService {


    @Autowired
    CustomerRepository repository;

    @Autowired
    CustomerAssembler customerAssembler;

    @Autowired
    CustomerIdentifierFactory customerIdentifierFactory;

    public List<CustomerDTO> getAllCustomers()
    {
        return customerAssembler.toResources(repository.findAll());
    }
    public CustomerDTO getCustomer(Long id)
    {
        return customerAssembler.toResource(repository.getOne(id));
    }

    public CustomerDTO createCustomer(String email, String URI)
    {
        Customer customer=null;
        if(email!=null && URI !=null)
        {
            customer = Customer.of(customerIdentifierFactory.nextCustomerID(),email,URI);
            repository.save(customer);
            return customerAssembler.toResource(customer);
        }
        else
        {
            return null;
        }
    }

}
