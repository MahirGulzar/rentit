package com.example.demo.inventory.application.services;


import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InventoryService {

    @Autowired
    PlantInventoryEntryRepository plantRepo;


    @Autowired
    PlantInventoryEntryAssembler plantInventoryEntryAssembler;

    public List<PlantInventoryEntryDTO> findAvailable(String name , LocalDate startDate, LocalDate endDate)
    {
        List<PlantInventoryEntry> res =plantRepo.findByComplicatedQuery(name.toLowerCase(),startDate,endDate);

        return plantInventoryEntryAssembler.toResources(res);
    }
}
