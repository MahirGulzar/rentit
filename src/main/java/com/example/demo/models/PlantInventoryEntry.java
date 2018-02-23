package com.example.demo.models;

import com.example.demo.models.objectvalue.Money;
import lombok.Data;
import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;

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
