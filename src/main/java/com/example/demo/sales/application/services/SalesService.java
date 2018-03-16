package com.example.demo.sales.application.services;


import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.common.domain.model.BusinessPeriod;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.services.PlantInventoryEntryAssembler;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.domain.model.PurchaseOrder;
import com.example.demo.sales.domain.model.factory.SalesIdentifierFactory;
import com.example.demo.sales.domain.repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SalesService {


    @Autowired
    PlantInventoryEntryRepository plantRepo;


    @Autowired
    PlantInventoryEntryAssembler plantInventoryEntryAssembler;


    @Autowired
    PurchaseOrderRepository orderRepo;
    @Autowired
    PurchaseOrderAssembler purchaseOrderAssembler;

    public List<PlantInventoryEntry> queryPlantCatalog(String name , BusinessPeriodDTO rentalPeriod)
    {
//        return plantRepo.findByNameContaining(name);

        return plantRepo.findByComplicatedQuery(name.toLowerCase(),rentalPeriod.getStartDate(),rentalPeriod.getEndDate());
    }




    public List<PlantInventoryEntryDTO> findAvailable(String plantName, LocalDate startDate, LocalDate endDate) {
        List<PlantInventoryEntry> res = plantRepo.findByComplicatedQuery(plantName,startDate, endDate);
        return plantInventoryEntryAssembler.toResources(res);
    }

    public PurchaseOrderDTO findPurchaseOrder(Long id) {
        PurchaseOrder po = orderRepo.findOne(id);
        return purchaseOrderAssembler.toResource(po);
    }

    public PurchaseOrderDTO createPO(PurchaseOrderDTO purchaseOrderDTO)
    {
        PurchaseOrder po = PurchaseOrder.of(
                plantRepo.findOne(purchaseOrderDTO.getPlant().get_id()),
                BusinessPeriod.of(
                        purchaseOrderDTO.getRentalPeriod().getStartDate(),
                        purchaseOrderDTO.getRentalPeriod().getEndDate()
                        ));


        orderRepo.save(po);

        return purchaseOrderAssembler.toResource(po);

    }



/*
@Autowired
PlantInventoryEntryRepository plantInventoryEntryRepository;

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    PurchaseOrderAssembler purchaseOrderAssembler;



    public PurchaseOrderDTO createPurchaseOrder(PurchaseOrderDTO partialPODTO) throws PlantNotFoundException {
        PlantInventoryEntry plant = plantInventoryEntryRepository.findOne(partialPODTO.getPlant().get_id());
        BusinessPeriod rentalPeriod = BusinessPeriod.of(partialPODTO.getRentalPeriod().getStartDate(), partialPODTO.getRentalPeriod().getEndDate());

        PurchaseOrder po = PurchaseOrder.of(
                plant,
                rentalPeriod);

        po = purchaseOrderRepository.save(po);

        return purchaseOrderAssembler.toResource(po);
    }
*/


}
