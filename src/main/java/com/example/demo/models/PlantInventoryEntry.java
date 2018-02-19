package com.example.demo.models;

import lombok.Data;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@Data
public class PlantInventoryEntry {

    @Id @GeneratedValue
    Long id;
    String name;
    String description;
    BigDecimal price;
}
