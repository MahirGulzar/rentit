package com.example.demo.sales.application.dto;

import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.sales.domain.model.POStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
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

    // Added for notification on BuiltIT
    String acceptHref;
    String rejectHref;

    String consumerURI;


    @JsonCreator // Added to test basic authentication
    public PurchaseOrderDTO() {
    }
}