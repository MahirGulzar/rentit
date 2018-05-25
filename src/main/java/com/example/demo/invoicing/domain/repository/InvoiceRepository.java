package com.example.demo.invoicing.domain.repository;


import com.example.demo.invoicing.domain.model.Invoice;
import com.example.demo.invoicing.domain.model.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, String> {
    Invoice findByPoID(Long poid);
    List<Invoice> findInvoiceByStatus(InvoiceStatus status);
}
