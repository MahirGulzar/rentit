package com.example.demo.models;

import com.example.demo.models.valueobject.Money;
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
