package com.example.demo.inventory.application.services;


import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.dto.PlantInventoryItemDTO;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.inventory.domain.model.PlantReservation;
import com.example.demo.inventory.domain.repository.InventoryRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryItemRepository;
import com.example.demo.inventory.domain.repository.PlantReservationRepository;
import com.example.demo.sales.domain.model.PurchaseOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
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
        PlantInventoryItem item=itemRepo.findPlantInventoryItemById(pid);
//        System.out.println(item.toString());
        return plantInventoryItemAssembler.toResource(itemRepo.findPlantInventoryItemById(pid));
    }

    public Resources<?> findAllPlantInventoryItems()
    {
        return  plantInventoryItemAssembler.toResources(itemRepo.findAll());
    }

    //------------------------------------------------------------------------------------------------------------
//
//    public Resources<?> findAvailablePOItems(Long plantID, LocalDate startDate, LocalDate endDate) {
//        List<PlantInventoryItem> res = itemRepo.findPlantsByEntriesAndSchedule(plantID,startDate,endDate);
//        return plantInventoryItemAssembler.toResources(res);
//    }
//
//
//    public Resources<?> findPlantInventoryItems(Long plantID) {
//        List<PlantInventoryItem> res = itemRepo.findPlantInventoryItemById(plantID);
//        return plantInventoryItemAssembler.toResources(res);
//    }
//

//
//
//    //------------------------------------------------------------------------------------------------------------
//
//    public PlantReservation createReservation(PurchaseOrder po,PlantInventoryItem item)
//    {
//        PlantReservation pr = PlantReservation.of(po,item);
//        reservationRepo.save(pr);
//        return pr;
//    }

}
