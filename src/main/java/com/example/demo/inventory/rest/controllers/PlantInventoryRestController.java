package com.example.demo.inventory.rest.controllers;

import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.dto.PlantInventoryItemDTO;
import com.example.demo.inventory.application.services.InventoryService;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class PlantInventoryRestController {

    @Autowired
    InventoryService inventoryService;


    @GetMapping("/plants/{pid}")
    public List<PlantInventoryItemDTO> findPlantInventoryItems(
            @PathVariable(name = "pid") Long id) {
        return inventoryService.findPlantInventoryItems(id);
    }

    @GetMapping("/entries/{eid}")
    public List<PlantInventoryEntryDTO> findPlantInventoryEntries(
            @PathVariable(name = "eid") Long id) {
        return inventoryService.findPlantInventoryEntries(id);
    }
}
