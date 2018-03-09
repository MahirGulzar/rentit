package com.example.demo.sales.application.dto;

// DTO = Data Transfer Object
// A placeholder

import com.example.demo.common.application.dto.BusinessPeriodDTO;
import lombok.Data;

@Data
public class CatalogQueryDTO {
    String name;
    BusinessPeriodDTO rentalPeriod;
}
