package com.example.demo.inventory.domain.model;

import com.example.demo.common.domain.model.Money;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
@Entity
@Data
public class PlantInventoryEntry {
    @Id @GeneratedValue
    Long id;

    String name;
    String description;

    @Column(precision = 8, scale = 2)
    BigDecimal price;
}