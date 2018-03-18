package com.example.demo.inventory.rest.controllers;

import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.dto.PlantInventoryItemDTO;
import com.example.demo.inventory.application.services.InventoryService;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class PlantInventoryRestController {

    @Autowired
    InventoryService inventoryService;

//    @GetMapping("/items")
//    public List<PlantInventoryItemDTO> findAvailableItems(
//            @RequestParam(name = "entry_id") Long id,
//            @RequestParam(name = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam(name = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
//        return inventoryService.findAvailableItems(id, startDate, endDate);
//    }
}
