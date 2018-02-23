package com.example.demo.models;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Data
public class PlantInventoryItem {
    String name;
    String description;

    @ManyToOne
    PlantInventoryEntry plant_info;


}
