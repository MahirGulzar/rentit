package com.example.demo.invoicing.application.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RemittanceDTO {
    Long poID;
    BigDecimal amount;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate date;
}
