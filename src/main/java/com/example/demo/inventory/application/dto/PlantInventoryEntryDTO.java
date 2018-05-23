package com.example.demo.inventory.application.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

import java.math.BigDecimal;


@Data

@Relation(value="plant", collectionRelation="plants")
public class PlantInventoryEntryDTO extends ResourceSupport{

    Long _id;
    String name;
    String description;
    BigDecimal price;

    @JsonCreator
    public PlantInventoryEntryDTO() {
    }
}
