package com.example.demo.inventory.application.services;



import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.dto.PlantInventoryItemDTO;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.inventory.domain.repository.InventoryRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryItemRepository;
import com.example.demo.inventory.domain.repository.PlantReservationRepository;

import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.xml.ws.Response;
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

    public ResponseEntity findPlantInventoryEntries(Long plantID) {

        PlantInventoryEntry plantInventoryEntry;
        try {
            plantInventoryEntry = entryRepo.getOne(plantID);
            System.out.println(plantInventoryEntry);
        }
        catch (Exception e)
        {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Plant not found Bad Identifier");
        }
        Resource<PlantInventoryEntryDTO> entryDTO = plantInventoryEntryAssembler.toResource(plantInventoryEntry);

        return new ResponseEntity<Resource<PlantInventoryEntryDTO>>(
                entryDTO,
                null,
                HttpStatus.OK);
    }

    public Resources<?> findAllPlantInventoryEntries() {
        return  plantInventoryEntryAssembler.toResources(entryRepo.findAll());
    }


    public ResponseEntity findItemById(Long pid)
    {

        PlantInventoryItem plantInventoryItem;
        try {
            plantInventoryItem = itemRepo.getOne(pid);
            System.out.println(plantInventoryItem);
        }
        catch (Exception e)
        {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Plant not found Bad Identifier");
        }
        Resource<PlantInventoryItemDTO> itemDto = plantInventoryItemAssembler.toResource(plantInventoryItem);

        return new ResponseEntity<Resource<PlantInventoryItemDTO>>(
                itemDto,
                null,
                HttpStatus.OK);

    }

    public Resources<?> findAllPlantInventoryItems()
    {
        return  plantInventoryItemAssembler.toResources(itemRepo.findAll());
    }

}
