package com.example.demo.inventory.rest.controllers;

import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.dto.PlantInventoryItemDTO;
import com.example.demo.inventory.application.services.InventoryService;
import com.example.demo.inventory.application.services.PlantInventoryEntryAssembler;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/inventory")
public class PlantInventoryRestController {

    @Autowired
    InventoryService inventoryService;

    @GetMapping("/plants/{pid}")
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER", "ROLE_EMPLOYEE"})
    public ResponseEntity<?> findPlantInventoryEntry(
            @PathVariable(name = "pid") Long id) {
        return inventoryService.findPlantInventoryEntries(id);
    }
    @GetMapping("/plants")
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER"})
    @ResponseStatus(HttpStatus.OK)
    public Resources<?> findAllPlantInventoryEntries() {
        return inventoryService.findAllPlantInventoryEntries();
    }


    //---------------------------------------------------------------------------


    @GetMapping("/items/{id}")
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER", "ROLE_EMPLOYEE"})
    public ResponseEntity<?> findPlantInventoryItem(
            @PathVariable(name = "id") Long id) {
        return inventoryService.findItemById(id);
    }

    @GetMapping("/items")
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER", "ROLE_EMPLOYEE"})
    @ResponseStatus(HttpStatus.OK)
    public Resources<?> findAllPlantInventoryItems() {
        return inventoryService.findAllPlantInventoryItems();
    }


}
