package com.example.demo.inventory.application;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data


@AllArgsConstructor(staticName = "of")
public class PlantInventoryEntryDTO {

    Long _id;
    String name;
    String description;
    BigDecimal price;

}
