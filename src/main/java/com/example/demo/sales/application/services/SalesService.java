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
import com.example.demo.inventory.domain.repository.InventoryRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryItemRepository;
import com.example.demo.inventory.domain.repository.PlantReservationRepository;
import com.example.demo.inventory.domain.validation.PlantInventoryEntryValidator;
import com.example.demo.sales.application.dto.POExtensionDTO;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.domain.model.POStatus;
import com.example.demo.sales.domain.model.PurchaseOrder;
import com.example.demo.sales.domain.model.factory.SalesIdentifierFactory;
import com.example.demo.sales.domain.repository.PurchaseOrderRepository;
import com.example.demo.sales.domain.validation.PurchaseOrderValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
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


    @Autowired
    InventoryRepository inventoryRepository;


    @Autowired
    POExtensionAssembler poExtensionAssembler;

    @Autowired
    SalesIdentifierFactory identifierFactory;
    /*
    Purchase Order Service Methods
     */
    //--------------------------------------------------------------------------------------------------------------

//    public List<PurchaseOrderDTO> findPurchaseOrderByStatus(String status)
//    {
//        return purchaseOrderAssembler.toResources(orderRepo.findPurchaseOrderByStatus(POStatus.valueOf(status)));
//    }



    public Resource<PurchaseOrderDTO> findPurchaseOrder(Long oid) {
        PurchaseOrder po = orderRepo.getOne(oid);
        return purchaseOrderAssembler.toResource(po);
    }

    public Resource<PurchaseOrderDTO> deletePurchaseOrder(Long oid) {
        PurchaseOrder order = orderRepo.getOne(oid);
        if(order.getStatus() == POStatus.PENDING){
            order.handleRejection();
        }
        else if(order.getStatus() == POStatus.OPEN){
            order.handleClose();
        }
        return purchaseOrderAssembler.toResource(order);
    }


    public Resource<PurchaseOrderDTO> updatePurchaseOrder(Long oid, PurchaseOrderDTO purchaseOrderDTO) {
        PurchaseOrder order = orderRepo.getOne(oid);
        PlantInventoryEntry plantItem = plantRepo.getOne(purchaseOrderDTO.getPlant().getContent().get_id());


        // TODO Validate data of PO DTO
        order.setPlant(plantItem);
        order.setRentalPeriod(purchaseOrderDTO.getRentalPeriod().asBusinessPeriod());
        order.setTotal(purchaseOrderDTO.getTotal());
        order.setStatus(purchaseOrderDTO.getStatus());
        orderRepo.save(order);
        return purchaseOrderAssembler.toResource(order);
    }

    public Resources<?> findAllPurchaseOrders(){
        return purchaseOrderAssembler.toResources(orderRepo.findAll());
    }


//    throws PlantNotFoundException, BindException
    public Resource<PurchaseOrderDTO> createPO(PurchaseOrderDTO purchaseOrderDTO)
    {
        PlantInventoryEntry plantInventoryEntry = plantRepo.getOne(purchaseOrderDTO.getPlant().getContent().get_id());
        try {
            if (plantInventoryEntry == null) {
                throw new PlantNotFoundException("Plant Not Found..");
            }
        }
        catch (PlantNotFoundException e)
        {
            // TODO handle exception
        }
        PurchaseOrder po = PurchaseOrder.of(
                identifierFactory.nextPOID(),
                plantInventoryEntry,
                BusinessPeriod.of(
                        purchaseOrderDTO.getRentalPeriod().getStartDate(),
                        purchaseOrderDTO.getRentalPeriod().getEndDate()
                        ));

        DataBinder binder = new DataBinder(po);

        binder.addValidators(new PurchaseOrderValidator(
                new BusinessPeriodValidator(),
                new BusinessPeriodIsInFutureValidator(),
                new PlantInventoryEntryValidator()));

        binder.validate();

        try {
            if (binder.getBindingResult().hasErrors()) {
                throw new BindException(binder.getBindingResult());
                }
        }
        catch (BindException e)
        {
            // TODO handle exception
        }

        orderRepo.save(po);

        return purchaseOrderAssembler.toResource(po);

    }


//    public PurchaseOrderDTO allocatePlant(Long oid,Long pid) {
//        PurchaseOrder po = orderRepo.findPurchaseOrderById(oid);
//        PlantInventoryItem item = inventoryService.findItemById(pid);
//
//        PlantReservation pr = inventoryService.createReservation(po,item);
//        po.createReservation(pr);
//        orderRepo.save(po);
//
//
//        return purchaseOrderAssembler.toResource(po);
//    }



    public Resource<PurchaseOrderDTO> rejectPurchaseOrder(Long oid) {
        PurchaseOrder po = orderRepo.findPurchaseOrderById(oid);
        po.handleRejection();

        orderRepo.save(po);

        return purchaseOrderAssembler.toResource(po);
    }

    //--------------------------------------------------------------------------------------------------------------



    /*
    Inventory Service Methods
     */
    //--------------------------------------------------------------------------------------------------------------
//    public List<PlantInventoryEntry> queryPlantCatalog(String name , BusinessPeriodDTO rentalPeriod)
//    {
//        return plantRepo.findByComplicatedQuery(name.toLowerCase(),rentalPeriod.getStartDate(),rentalPeriod.getEndDate());
//    }
//
//
//    public List<PlantInventoryItemDTO> findAvailablePOItems(String oid)
//    {
//        PurchaseOrder po = orderRepo.findPurchaseOrderById(oid);
//        List<PlantInventoryItemDTO> res = inventoryService.findAvailablePOItems(po.getPlant().getId(),po.getRentalPeriod().getStartDate(),po.getRentalPeriod().getEndDate());
//        return res;
//    }



    //NEW Methods....

    //---------------------------------------------------------------------------------------

    public Resource<PurchaseOrderDTO> allocatePlantToPurchaseOrder(Long id){
        PurchaseOrder order = orderRepo.getOne(id);
        LocalDate startDate = order.getRentalPeriod().getStartDate();
        LocalDate endDate = order.getRentalPeriod().getEndDate();
        List<PlantInventoryItem> items = inventoryRepository.findAvailableItems(order.getPlant(), startDate, endDate);

        if(!items.isEmpty()){
            PlantReservation reservation = new PlantReservation();
            reservation.setPlant(items.get(0));
            reservation.setSchedule(BusinessPeriod.of(startDate, endDate));
            reservationRepo.save(reservation);

            order.registerFirstAllocation(reservation);

        }
        else{
            order.handleRejection();
        }
        orderRepo.save(order);
        return purchaseOrderAssembler.toResource(order);
    }



    public Resource<PurchaseOrderDTO> requestPurchaseExtension(Long id, LocalDate endDate) {
        PurchaseOrder order = orderRepo.getOne(id);
        order.requestExtension(endDate);
        orderRepo.save(order);
        return purchaseOrderAssembler.toResource(order);
    }

    public Resource<PurchaseOrderDTO> acceptPurchaseExtension(Long id, PlantInventoryItemDTO plantInventoryItemDTO) {

        PurchaseOrder order = orderRepo.getOne(id);
        PlantInventoryItem item = itemRepo.getOne(plantInventoryItemDTO.get_id());
        PlantReservation plantReservation = new PlantReservation();
        plantReservation.setPlant(item);
        // todo accept purchase order extension
        return purchaseOrderAssembler.toResource(order);
    }

    public Resource<PurchaseOrderDTO> closePurchaseOrder(Long oid) {
        PurchaseOrder po = orderRepo.findPurchaseOrderById(oid);
        po.handleClose();

        orderRepo.save(po);

        return purchaseOrderAssembler.toResource(po);
    }

    public Resources<Resource<POExtensionDTO>> fetchPurchaseOrderExtensions(Long oid) {
        PurchaseOrder po = orderRepo.findPurchaseOrderById(oid);
        return poExtensionAssembler.toResources(po.getExtensions(), po);
    }


}
