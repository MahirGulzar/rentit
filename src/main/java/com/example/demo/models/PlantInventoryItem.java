package com.example.demo.models;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Data
public class PlantInventoryItem {
    @Id
    @GeneratedValue
    Long id;

    String name;
    String description;

    @ManyToOne
    PlantInventoryEntry plant_info;


}
