package com.example.demo.inventory.utils;

import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlantsWithCount {
    PlantInventoryEntry entry;
    Long count;
}