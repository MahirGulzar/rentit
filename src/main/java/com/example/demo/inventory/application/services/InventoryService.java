package com.example.demo.inventory.application.services;


import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.dto.PlantInventoryItemDTO;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InventoryService {

    @Autowired
    PlantInventoryEntryRepository plantRepo;
    @Autowired
    PlantInventoryItemRepository itemRepo;


    @Autowired
    PlantInventoryItemAssembler plantInventoryItemAssembler;
    @Autowired
    PlantInventoryEntryAssembler plantInventoryEntryAssembler;

    public List<PlantInventoryEntryDTO> findAvailable(String plantName, LocalDate startDate, LocalDate endDate) {
        List<PlantInventoryEntry> res = plantRepo.findByComplicatedQuery(plantName,startDate, endDate);
        return plantInventoryEntryAssembler.toResources(res);
    }


    public List<PlantInventoryItemDTO> findAvailableItems(String plantName, LocalDate startDate, LocalDate endDate) {
        List<PlantInventoryItem> res = itemRepo.findAll();
        return plantInventoryItemAssembler.toResources(res);
    }

}
