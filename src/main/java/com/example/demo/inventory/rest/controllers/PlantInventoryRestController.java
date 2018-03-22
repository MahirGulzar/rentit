package com.example.demo.inventory.rest.controllers;

import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.dto.PlantInventoryItemDTO;
import com.example.demo.inventory.application.services.InventoryService;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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
    @GetMapping("/plants")
    @ResponseStatus(HttpStatus.OK)
    public List<PlantInventoryItemDTO> findAllPlantInventoryItems() {
        return inventoryService.findAllPlantInventoryItems();
    }


    //---------------------------------------------------------------------------


    @GetMapping("/entries/{eid}")
    public List<PlantInventoryEntryDTO> findPlantInventoryEntries(
            @PathVariable(name = "eid") Long id) {
        return inventoryService.findPlantInventoryEntries(id);
    }

    @GetMapping("/entries")
    @ResponseStatus(HttpStatus.OK)
    public List<PlantInventoryEntryDTO> findAllPlantInventoryEntries() {
        return inventoryService.findAllPlantInventoryEntries();
    }


}
