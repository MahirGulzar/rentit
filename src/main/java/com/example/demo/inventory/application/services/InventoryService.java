package com.example.demo.inventory.application.services;



import com.example.demo.inventory.domain.repository.InventoryRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryItemRepository;
import com.example.demo.inventory.domain.repository.PlantReservationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class InventoryService {

    @Autowired
    PlantInventoryEntryRepository entryRepo;
    @Autowired
    PlantInventoryItemRepository itemRepo;
    @Autowired
    PlantReservationRepository reservationRepo;


    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    PlantInventoryItemAssembler plantInventoryItemAssembler;
    @Autowired
    PlantInventoryEntryAssembler plantInventoryEntryAssembler;

    public Resources<?> findAvailable(String plantName, LocalDate startDate, LocalDate endDate) {
        return plantInventoryEntryAssembler.toResources(inventoryRepository.findAvailablePlants(plantName,startDate, endDate));
    }

    public Resource<?> findPlantInventoryEntries(Long plantID) {
        return plantInventoryEntryAssembler.toResource(entryRepo.getOne(plantID));
    }

    public Resources<?> findAllPlantInventoryEntries() {
        return  plantInventoryEntryAssembler.toResources(entryRepo.findAll());
    }


    public Resource<?> findItemById(Long pid)
    {
        return plantInventoryItemAssembler.toResource(itemRepo.findPlantInventoryItemById(pid));
    }

    public Resources<?> findAllPlantInventoryItems()
    {
        return  plantInventoryItemAssembler.toResources(itemRepo.findAll());
    }

}
