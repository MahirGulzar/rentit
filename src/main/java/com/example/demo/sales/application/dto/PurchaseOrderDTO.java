package com.example.demo.sales.application.dto;

import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.sales.domain.model.POStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

import javax.persistence.Column;
import java.math.BigDecimal;

@Data
@Relation(value = "order", collectionRelation = "orders")
public class PurchaseOrderDTO extends ResourceSupport {
    Long _id;

    Resource<PlantInventoryEntryDTO> plant;
    BusinessPeriodDTO rentalPeriod;

    @Column(precision = 8, scale = 2)
    BigDecimal total;
    POStatus status;
}