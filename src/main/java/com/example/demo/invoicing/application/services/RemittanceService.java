package com.example.demo.invoicing.application.services;

import com.example.demo.invoicing.application.dto.RemittanceDTO;
import com.example.demo.invoicing.domain.model.Invoice;
import com.example.demo.invoicing.domain.model.InvoiceStatus;
import com.example.demo.invoicing.domain.repository.InvoiceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class RemittanceService {

    @Autowired
    @Qualifier("_halObjectMapper")
    ObjectMapper mapper;

    @Autowired
    InvoiceRepository invoiceRepository;

    public void processRemittance(String remittanceString) {

        RemittanceDTO remittanceDTO;
        try {

            remittanceDTO = mapper.readValue(remittanceString, RemittanceDTO.class);

            Invoice invoice = invoiceRepository.getOne(remittanceDTO.getPoID());

            if(invoice != null/* && invoice.getAmount().compareTo(remittanceDTO.getAmount()) == 0*/){

                invoice.setStatus(InvoiceStatus.PAID);
                invoiceRepository.save(invoice);
            }

            //TODO: implemented failing case here as well.

        } catch (IOException e) {
            e.printStackTrace();
        }



    }


}
