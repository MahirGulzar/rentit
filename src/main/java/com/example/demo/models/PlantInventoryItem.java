package com.example.demo.models;


import lombok.Data;

import javax.persistence.Entity;

@Entity
@Data
public class PlantInventoryItem {
    String name;
    String description;

}
