package com.example.demo.sales.application.services;


import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.common.domain.model.BusinessPeriod;
import com.example.demo.common.domain.validation.BusinessPeriodIsInFutureValidator;
import com.example.demo.common.domain.validation.BusinessPeriodValidator;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.dto.PlantInventoryItemDTO;
import com.example.demo.inventory.application.exceptions.PlantNotFoundException;
import com.example.demo.inventory.application.services.InventoryService;
import com.example.demo.inventory.application.services.PlantInventoryEntryAssembler;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.inventory.domain.model.PlantReservation;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryItemRepository;
import com.example.demo.inventory.domain.repository.PlantReservationRepository;
import com.example.demo.inventory.domain.validation.PlantInventoryEntryValidator;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.domain.model.POStatus;
import com.example.demo.sales.domain.model.PurchaseOrder;
import com.example.demo.sales.domain.model.factory.SalesIdentifierFactory;
import com.example.demo.sales.domain.repository.PurchaseOrderRepository;
import com.example.demo.sales.domain.validation.PurchaseOrderValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.DataBinder;

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


    public List<PurchaseOrderDTO> findAllPurchaseOrders(){
        return purchaseOrderAssembler.toResources(orderRepo.findAll());
    }


    public PurchaseOrderDTO createPO(PurchaseOrderDTO purchaseOrderDTO) throws PlantNotFoundException, BindException
    {

        PlantInventoryEntry plantInventoryEntry = plantRepo.findOne(purchaseOrderDTO.getPlant().get_id());
        if(plantInventoryEntry == null) {
            throw new PlantNotFoundException("Plant not found", purchaseOrderDTO);
        }

        PurchaseOrder po = PurchaseOrder.of(
                plantRepo.findOne(purchaseOrderDTO.getPlant().get_id()),
                BusinessPeriod.of(
                        purchaseOrderDTO.getRentalPeriod().getStartDate(),
                        purchaseOrderDTO.getRentalPeriod().getEndDate()
                        ));

        DataBinder binder = new DataBinder(po);

        binder.addValidators(new PurchaseOrderValidator(
                new PlantInventoryEntryValidator(),
                new BusinessPeriodValidator(),
                new BusinessPeriodIsInFutureValidator()));

        binder.validate();

        if (binder.getBindingResult().hasErrors()) {
            //If there was a data validation error, nothing happens. If the plant is just not available in the given date, PO is created.
            throw new BindException(binder.getBindingResult());
        }

        orderRepo.save(po);

        return purchaseOrderAssembler.toResource(po);

    }


    public PurchaseOrderDTO allocatePlant(Long oid,Long pid) {
        PurchaseOrder po = orderRepo.findOne(oid);
        PlantInventoryItem item = inventoryService.findItemById(pid);

        PlantReservation pr = inventoryService.createReservation(po,item);
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


    public List<PlantInventoryItemDTO> findAvailablePOItems(Long oid)
    {
        PurchaseOrder po = orderRepo.findOne(oid);
        List<PlantInventoryItemDTO> res = inventoryService.findAvailablePOItems(po.getPlant().getId(),po.getRentalPeriod().getStartDate(),po.getRentalPeriod().getEndDate());
        return res;
    }






}
