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
    PlantInventoryEntryRepository entryRepo;
    @Autowired
    PlantInventoryItemRepository itemRepo;


    @Autowired
    PlantInventoryItemAssembler plantInventoryItemAssembler;
    @Autowired
    PlantInventoryEntryAssembler plantInventoryEntryAssembler;

    public List<PlantInventoryEntryDTO> findAvailable(String plantName, LocalDate startDate, LocalDate endDate) {
        List<PlantInventoryEntry> res = entryRepo.findByComplicatedQuery(plantName,startDate, endDate);
        return plantInventoryEntryAssembler.toResources(res);
    }

    public List<PlantInventoryEntryDTO> findPlantInventoryEntries(Long plantID) {
        List<PlantInventoryEntry> res = entryRepo.findPlantInventoryEntryById(plantID);
        return plantInventoryEntryAssembler.toResources(res);
    }





    //------------------------------------------------------------------------------------------------------------

    public List<PlantInventoryItemDTO> findAvailablePOItems(Long plantID, LocalDate startDate, LocalDate endDate) {
        List<PlantInventoryItem> res = itemRepo.findPlantsByEntriesAndSchedule(plantID,startDate,endDate);
        return plantInventoryItemAssembler.toResources(res);
    }


    public List<PlantInventoryItemDTO> findPlantInventoryItems(Long plantID) {
        List<PlantInventoryItem> res = itemRepo.findPlantInventoryItemById(plantID);
        return plantInventoryItemAssembler.toResources(res);
    }

}
