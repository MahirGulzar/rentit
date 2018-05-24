package com.example.demo.sales.application.services;


import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.common.application.exceptions.PurchaseOrderNotFoundException;
import com.example.demo.common.domain.model.BusinessPeriod;
import com.example.demo.common.domain.validation.BusinessPeriodIsInFutureValidator;
import com.example.demo.common.domain.validation.BusinessPeriodValidator;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.dto.PlantInventoryItemDTO;
import com.example.demo.inventory.application.exceptions.PlantNotFoundException;
import com.example.demo.inventory.application.services.InventoryService;
import com.example.demo.inventory.application.services.PlantInventoryEntryAssembler;
import com.example.demo.inventory.application.services.PlantInventoryItemAssembler;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.inventory.domain.model.PlantReservation;
import com.example.demo.inventory.domain.repository.InventoryRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryItemRepository;
import com.example.demo.inventory.domain.repository.PlantReservationRepository;
import com.example.demo.inventory.domain.validation.PlantInventoryEntryValidator;
import com.example.demo.invoicing.application.dto.InvoiceDTO;
import com.example.demo.invoicing.application.integrations.gateways.InvoicingGateway;
import com.example.demo.invoicing.application.services.InvoiceAssembler;
import com.example.demo.invoicing.application.services.InvoiceService;
import com.example.demo.invoicing.domain.model.Invoice;
import com.example.demo.invoicing.domain.model.InvoiceStatus;
import com.example.demo.invoicing.domain.repository.InvoiceRepository;
import com.example.demo.invoicing.infrastructure.InvoiceIdentifierFactory;
import com.example.demo.sales.application.dto.POExtensionDTO;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.domain.model.POStatus;
import com.example.demo.sales.domain.model.PurchaseOrder;
import com.example.demo.sales.domain.model.factory.SalesIdentifierFactory;
import com.example.demo.sales.domain.repository.PurchaseOrderRepository;
import com.example.demo.sales.domain.validation.PurchaseOrderValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.DataBinder;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
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
    PlantInventoryItemAssembler plantInventoryItemAssembler;


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


    @Autowired
    RestTemplate restTemplate;

    @Autowired
    InvoiceService invoiceService;
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
//            order.handleRejection(); todo verifyy
            order.handleClose();
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
                        ),
                purchaseOrderDTO.getAcceptHref(),
                purchaseOrderDTO.getRejectHref()
                );

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
        if(po.getRejectHref()!=null) {
            restTemplate.delete(po.getRejectHref());
        }
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
        System.out.println("Before sending request...");
        if(order.getAcceptHref()!=null) {
            ResponseEntity<?> result = restTemplate.postForEntity(order.getAcceptHref(), null, PurchaseOrderDTO.class);
        }
        System.out.println("After sending request...");
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
        System.out.println(order);
        PlantInventoryItem item = itemRepo.findPlantInventoryItemById(plantInventoryItemDTO.get_id());

        if(inventoryRepository.isAvailableFor(item,order.getRentalPeriod().getEndDate().plusDays(1),order.pendingExtensionEndDate()))
        {
            System.out.println("Item is available in these dates....");
            PlantReservation plantReservation = new PlantReservation();
            plantReservation.setPlant(item);
            System.out.println(order.pendingExtensionEndDate());
            plantReservation.setSchedule(BusinessPeriod.of(order.getRentalPeriod().getEndDate().plusDays(1), order.pendingExtensionEndDate()));
            reservationRepo.save(plantReservation);

//            order.setRentalPeriod(BusinessPeriod.of(order.getRentalPeriod().getStartDate(),order.pendingExtensionEndDate()));
            order.acceptExtension(plantReservation);
            orderRepo.save(order);
        }
        else {

            System.out.println("Item not available in these dates checking for replacement....");
            LocalDate startDate = order.getRentalPeriod().getStartDate();
            LocalDate endDate = order.getRentalPeriod().getEndDate();
            List<PlantInventoryItem> items = inventoryRepository.findReplacementItems(order.getPlant().getName(), startDate, endDate);

            if(!items.isEmpty()){

                System.out.println("Current plant price:---> "+item.getPlantInfo().getPrice());
                System.out.println("Replacement plant price:---> "+items.get(0).getPlantInfo().getPrice());
                if(item.getPlantInfo().getPrice().compareTo(items.get(0).getPlantInfo().getPrice())<0) {


                    BigDecimal reducedPrice = (items.get(0).getPlantInfo().getPrice()
                            .subtract(item.getPlantInfo().getPrice()));
                    System.out.println(reducedPrice);
//                    reducedPrice = reducedPrice.divide(item.getPlantInfo().getPrice());
//                    System.out.println(reducedPrice);
                    float decimalval = reducedPrice.floatValue();
                    float prevPlantValue=item.getPlantInfo().getPrice().floatValue();
                    System.out.println("Decimal value of reduced right now = "+decimalval);
                    System.out.println("Decimal value of previous plant  = "+item.getPlantInfo().getPrice().floatValue());
                    decimalval = decimalval/prevPlantValue;
                    System.out.println("Division value = "+decimalval);
                    decimalval = decimalval*100.0f;
//                    reducedPrice = reducedPrice.multiply(new BigDecimal(100));
                    System.out.println(decimalval);
                    if (decimalval<=30) {
                        System.out.println("Replacement found for loss less than 30%....");

                        PlantReservation plantReservation = new PlantReservation();
                        plantReservation.setPlant(item);
                        System.out.println(order.pendingExtensionEndDate());
                        plantReservation.setSchedule(BusinessPeriod.of(order.getRentalPeriod().getEndDate().plusDays(1), order.pendingExtensionEndDate()));
                        reservationRepo.save(plantReservation);

                        order.acceptExtension(plantReservation);
                        orderRepo.save(order);
                    } else {
                        System.out.println("Replacement not found for loss less than 30%....");
                    }
                }
                else
                {
                    System.out.println("Replacement found with no loss ...");
                    PlantReservation plantReservation = new PlantReservation();
                    plantReservation.setPlant(item);
                    System.out.println(order.pendingExtensionEndDate());
                    plantReservation.setSchedule(BusinessPeriod.of(order.getRentalPeriod().getEndDate().plusDays(1), order.pendingExtensionEndDate()));
                    reservationRepo.save(plantReservation);

                    order.acceptExtension(plantReservation);
                    orderRepo.save(order);
                }

            }
            else{
                System.out.println("Replacement not found at all....");
            }

        }

        return purchaseOrderAssembler.toResource(order);
    }


    public Resource<PurchaseOrderDTO> rejectPurchaseExtension(Long id) {

        PurchaseOrder order = orderRepo.getOne(id);
        if(!order.rejectCurrentExtension())
        {
            // No Extension found
        }
        orderRepo.save(order);
        return purchaseOrderAssembler.toResource(order);
    }

    public Resource<PurchaseOrderDTO> closePurchaseOrder(Long oid) {
        PurchaseOrder po = orderRepo.findPurchaseOrderById(oid);
        po.handleClose();

        orderRepo.save(po);

        return purchaseOrderAssembler.toResource(po);
    }

    public Resources<?> fetchPurchaseOrderExtensions(Long oid) {
        PurchaseOrder po = orderRepo.findPurchaseOrderById(oid);
        return poExtensionAssembler.toResources(po.getExtensions(), po);
    }



    // ------------------------ Project Methods here -------------

    public String cancelPO(Long id) throws PurchaseOrderNotFoundException, BindException {
        PurchaseOrder purchaseOrder = orderRepo.getOne(id);
        if(purchaseOrder == null) throw new PurchaseOrderNotFoundException(id);

        if(purchaseOrder.getStatus() == POStatus.OPEN ||
                purchaseOrder.getStatus() == POStatus.PENDING){
            purchaseOrder.setStatus(POStatus.CANCELLED);
            orderRepo.save(purchaseOrder);

            return "PO cancelled.";
        }
        return "Given PO cannot be cancelled now.";
    }



    public Resource<PurchaseOrderDTO> distpatchPO(Long id) throws PurchaseOrderNotFoundException {
        PurchaseOrder purchaseOrder = orderRepo.getOne(id);
        if(purchaseOrder == null) throw new PurchaseOrderNotFoundException(id);

        // Change status only if PO is in ACCEPTED state, otherwise, for now, send the PO back as it is.

        if(purchaseOrder.getStatus() == POStatus.OPEN ){
            purchaseOrder.setStatus(POStatus.DISPATCHED);
            orderRepo.save(purchaseOrder);
        }

        return purchaseOrderAssembler.toResource(purchaseOrder);
    }

    public Resource<PurchaseOrderDTO> deliverPO(Long id) throws PurchaseOrderNotFoundException {
        PurchaseOrder purchaseOrder = orderRepo.getOne(id);
        if(purchaseOrder == null) throw new PurchaseOrderNotFoundException(id);

        // if PO is in ACCEPTED or DISPATCHED state, otherwise, for now, send the PO back as it is.

        if(purchaseOrder.getStatus() == POStatus.DISPATCHED || purchaseOrder.getStatus() == POStatus.OPEN){
            purchaseOrder.setStatus(POStatus.DELIVERED);
            orderRepo.save(purchaseOrder);
        }

        return purchaseOrderAssembler.toResource(purchaseOrder);
    }

    public Resource<PurchaseOrderDTO> customerRejectedPO(Long id) throws PurchaseOrderNotFoundException {
        PurchaseOrder purchaseOrder = orderRepo.getOne(id);
        if(purchaseOrder == null) throw new PurchaseOrderNotFoundException(id);

        // Change status only if PO is in DISPATCHED state, otherwise, for now, send the PO back as it is.

        if(purchaseOrder.getStatus() == POStatus.DISPATCHED ){
            purchaseOrder.setStatus(POStatus.REJECTED_BY_CUSTOMER);
            orderRepo.save(purchaseOrder);
        }

        return purchaseOrderAssembler.toResource(purchaseOrder);
    }







    public Resource<PurchaseOrderDTO> returnPO(Long id) throws PurchaseOrderNotFoundException {
        PurchaseOrder purchaseOrder = orderRepo.getOne(id);
        if(purchaseOrder == null) throw new PurchaseOrderNotFoundException(id);

        // Change status only if PO is in DELIVERED state, otherwise, for now, send the PO back as it is.

        if(purchaseOrder.getStatus() == POStatus.DELIVERED ){

            //send invoice as plant is returned
            //TODO Send invoice
            invoiceService.sendInvoice(purchaseOrderAssembler.toResource(purchaseOrder));


            purchaseOrder.setStatus(POStatus.RETURNED);
            orderRepo.save(purchaseOrder);

        }

        return purchaseOrderAssembler.toResource(purchaseOrder);
    }


    public Resources<?> findPlantsToDispatch(LocalDate givenDate){
        return plantInventoryItemAssembler.toResources(orderRepo.findPlantsToDispatch(givenDate));
    }

}
