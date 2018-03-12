package com.example.demo.sales.application.dto;

import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.sales.domain.model.POStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.ResourceSupport;

import javax.persistence.Column;
import java.math.BigDecimal;

@Data
@NoArgsConstructor(force=true)
public class PurchaseOrderDTO {
    Long id;
    PlantInventoryEntryDTO plant;
    @Column(precision = 8, scale = 2)
    BigDecimal total;
    POStatus status;
    BusinessPeriodDTO rentalPeriod;
}
