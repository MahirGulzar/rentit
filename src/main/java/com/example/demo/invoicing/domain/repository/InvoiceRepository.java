package com.example.demo.invoicing.domain.repository;


import com.example.demo.invoicing.domain.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, String> {

}
