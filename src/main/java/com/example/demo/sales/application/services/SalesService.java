package com.example.demo.sales.application.services;


import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.common.domain.model.BusinessPeriod;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.dto.PlantInventoryItemDTO;
import com.example.demo.inventory.application.services.InventoryService;
import com.example.demo.inventory.application.services.PlantInventoryEntryAssembler;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.inventory.domain.model.PlantReservation;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryItemRepository;
import com.example.demo.inventory.domain.repository.PlantReservationRepository;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.domain.model.POStatus;
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
    InventoryService inventoryService;


    @Autowired
    PlantReservationRepository reservationRepo;

    @Autowired
    PlantInventoryItemRepository itemRepo;


    @Autowired
    PlantInventoryEntryRepository plantRepo;
    @Autowired
    PlantInventoryEntryAssembler plantInventoryEntryAssembler;


    @Autowired
    PurchaseOrderRepository orderRepo;
    @Autowired
    PurchaseOrderAssembler purchaseOrderAssembler;

    /*
    Purchase Order Service Methods
     */
    //--------------------------------------------------------------------------------------------------------------

    public List<PurchaseOrderDTO> findPurchaseOrderByStatus(String status)
    {
        return purchaseOrderAssembler.toResources(orderRepo.findPurchaseOrderByStatus(POStatus.valueOf(status)));
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


    public PurchaseOrderDTO allocatePlant(Long oid,Long pid) {
        PurchaseOrder po = orderRepo.findOne(oid);
        PlantInventoryItem item = itemRepo.findOne(pid);

        PlantReservation pr = PlantReservation.of(po,item);
        reservationRepo.save(pr);

        po.createReservation(pr);
        orderRepo.save(po);


        return purchaseOrderAssembler.toResource(po);
    }



    public PurchaseOrderDTO rejectPurchaseOrder(Long oid) {
        PurchaseOrder po = orderRepo.findOne(oid);
        po.handleRejection();

        orderRepo.save(po);

        return purchaseOrderAssembler.toResource(po);
    }

    //--------------------------------------------------------------------------------------------------------------



    /*
    Inventory Service Methods
     */
    //--------------------------------------------------------------------------------------------------------------
    public List<PlantInventoryEntry> queryPlantCatalog(String name , BusinessPeriodDTO rentalPeriod)
    {
        return plantRepo.findByComplicatedQuery(name.toLowerCase(),rentalPeriod.getStartDate(),rentalPeriod.getEndDate());
    }


    public List<PlantInventoryItemDTO> findAvailablePOItems(Long poID, LocalDate startDate, LocalDate endDate)
    {
        PurchaseOrder po = orderRepo.findOne(poID);
        List<PlantInventoryItemDTO> res = inventoryService.findAvailablePOItems(po.getPlant().getId(),startDate,endDate);
        return res;
    }






}
