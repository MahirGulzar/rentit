package com.example.demo.inventory.domain.model;

import com.example.demo.common.domain.model.Money;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import javax.persistence.*;

@Entity
//@Data
@Getter
@AllArgsConstructor(staticName = "of")
public class PlantInventoryEntry {

    @Id @GeneratedValue
    Long id;

    String name;
    String description;

    @Embedded
    Money price;

}
