package com.example.demo.inventory.application.dto;


import com.example.demo.common.domain.model.Money;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.ResourceSupport;

import java.math.BigDecimal;


@NoArgsConstructor(force = false, access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
@Data
public class PlantInventoryEntryDTO extends ResourceSupport{

    Long _id;
    String name;
    String description;
    Money price;

}
