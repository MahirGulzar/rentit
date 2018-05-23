package com.example.demo.invoicing.application.dto;

import com.example.demo.invoicing.domain.model.InvoiceStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.ResourceSupport;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
public class InvoiceDTO extends ResourceSupport {
    String _id;
    Long poID;
    BigDecimal amount;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate dueDate;
    InvoiceStatus status;
}

