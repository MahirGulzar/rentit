package com.example.demo.invoicing.application.services;


import com.example.demo.invoicing.application.dto.InvoiceDTO;
import com.example.demo.invoicing.domain.model.Invoice;
import com.example.demo.invoicing.rest.InvoiceRestController;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;

@Service
public class InvoiceAssembler extends ResourceAssemblerSupport<Invoice, InvoiceDTO> {

    public InvoiceAssembler() {
        super(InvoiceRestController.class, InvoiceDTO.class);
    }

    @Override
    public InvoiceDTO toResource(Invoice invoice) {
        InvoiceDTO dto = createResourceWithId(invoice.getId(), invoice);

        dto.set_id(invoice.getId());
        dto.setPoID(invoice.getPoID());
        dto.setDueDate(invoice.getDueDate());
        dto.setAmount(invoice.getAmount());
        dto.setStatus(invoice.getStatus());

        return dto;
    }
}
