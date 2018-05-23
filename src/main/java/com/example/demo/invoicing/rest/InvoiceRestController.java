package com.example.demo.invoicing.rest;


import com.example.demo.invoicing.application.dto.InvoiceDTO;
import com.example.demo.invoicing.application.services.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/invoices")
public class InvoiceRestController {

    @Autowired
    InvoiceService invoiceService;

    @GetMapping()
    public List<InvoiceDTO> getAllInvoices() {
        return invoiceService.getInvoices();
    }

    @GetMapping("/{id}")
    public InvoiceDTO getSingleInvoices(@PathVariable("id") String id) {
        return invoiceService.getsingleInvoices(id);
    }

}
