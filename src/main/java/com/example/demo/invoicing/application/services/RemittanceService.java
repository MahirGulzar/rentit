package com.example.demo.invoicing.application.services;

import com.example.demo.invoicing.application.dto.RemittanceDTO;
import com.example.demo.invoicing.domain.model.Invoice;
import com.example.demo.invoicing.domain.model.InvoiceStatus;
import com.example.demo.invoicing.domain.repository.InvoiceRepository;
import com.example.demo.sales.domain.model.POStatus;
import com.example.demo.sales.domain.model.PurchaseOrder;
import com.example.demo.sales.domain.repository.PurchaseOrderRepository;
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

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;


    public void processRemittance(String remittanceString) {

        RemittanceDTO remittanceDTO;
        try {

            remittanceDTO = mapper.readValue(remittanceString, RemittanceDTO.class);

            String poidHref = remittanceDTO.getPoID().toString();

            Long poID = Long.parseLong(poidHref.substring(poidHref.lastIndexOf("/")+1,poidHref.length()));

            Invoice invoice = invoiceRepository.findByPoID(poID);

            PurchaseOrder purchaseOrder = purchaseOrderRepository.getOne(poID);

            purchaseOrder.setStatus(POStatus.INVOICED);

            purchaseOrderRepository.save(purchaseOrder);


            if(invoice != null){

                invoice.setStatus(InvoiceStatus.PAID);
                invoiceRepository.save(invoice);
            }

            //TODO: implemented failing case here as well.

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
