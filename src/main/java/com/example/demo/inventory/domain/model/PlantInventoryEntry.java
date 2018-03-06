package com.example.demo.inventory.domain.model;

import com.example.demo.common.domain.model.Money;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class PlantInventoryEntry {

    @Id @GeneratedValue
    Long id;
    String name;
    String description;
    @Embedded
    Money price;

}
