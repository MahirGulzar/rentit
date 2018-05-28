package com.example.demo.sales.application.services;


import com.example.demo.common.application.exceptions.PurchaseOrderNotFoundException;
import com.example.demo.common.domain.model.BusinessPeriod;
import com.example.demo.common.domain.validation.BusinessPeriodIsInFutureValidator;
import com.example.demo.common.domain.validation.BusinessPeriodValidator;
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
import com.example.demo.invoicing.application.services.InvoiceService;
import com.example.demo.mailing.domain.repository.CustomerRepository;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.application.factory.ReservationFactory;
import com.example.demo.sales.application.integration.gateways.POGateway;
import com.example.demo.sales.domain.model.POStatus;
import com.example.demo.sales.domain.model.PurchaseOrder;
import com.example.demo.sales.domain.model.factory.SalesIdentifierFactory;
import com.example.demo.sales.domain.repository.PurchaseOrderRepository;
import com.example.demo.sales.domain.validation.PurchaseOrderValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.DataBinder;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
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

    @Autowired
    POGateway poGateway;

    @Autowired
    CustomerRepository customerRepository;


    @Autowired
    ReservationFactory reservationFactory;

    @Autowired
    @Qualifier("_halObjectMapper")
    ObjectMapper mapper;

    @Value("${gmail.from}")
    String emailFrom;




    public ResponseEntity findPurchaseOrder(Long oid) {
        PurchaseOrder po = orderRepo.getOne(oid);

        try {
            po = orderRepo.findPurchaseOrderById(oid);
            System.out.println(po);
        }
        catch (Exception e)
        {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("PO not found Bad Identifier");
        }

        Resource<PurchaseOrderDTO> returnDTO = purchaseOrderAssembler.toResource(po);
        return new ResponseEntity<Resource<PurchaseOrderDTO>>(
                returnDTO,
                null,
                HttpStatus.OK);
    }

    public Resource<PurchaseOrderDTO> deletePurchaseOrder(Long oid) {
        PurchaseOrder order = orderRepo.getOne(oid);
        if(order.getStatus() == POStatus.PENDING){
            order.handleClose();
        }
        else if(order.getStatus() == POStatus.OPEN){
            order.handleClose();
        }
        else if(order.getStatus() == POStatus.REJECTED){
            order.handleClose();
        }
        else if(order.getStatus() == POStatus.REJECTED_BY_CUSTOMER){
            order.handleClose();
        }
        else if(order.getStatus() == POStatus.INVOICED){
            order.handleClose();
        }
        else if(order.getStatus() == POStatus.RETURNED){
            order.handleClose();
        }
        Resource<PurchaseOrderDTO> purchaseOrderDTO=purchaseOrderAssembler.toResource(order);
        sendPONotication(purchaseOrderDTO);
        return purchaseOrderDTO;
    }


    public ResponseEntity updatePurchaseOrder(Long oid, PurchaseOrderDTO purchaseOrderDTO) {
        PurchaseOrder order;
        PlantInventoryEntry plantItem;
        try {
            order= orderRepo.getOne(oid);
            plantItem= plantRepo.getOne(purchaseOrderDTO.getPlant().getContent().get_id());
        }
        catch (Exception e)
        {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getLocalizedMessage());
        }


        // TODO Validate data of PO DTO
        order.setPlant(plantItem);
        order.setRentalPeriod(purchaseOrderDTO.getRentalPeriod().asBusinessPeriod());
        order.setTotal(purchaseOrderDTO.getTotal());
        order.setStatus(purchaseOrderDTO.getStatus());

        DataBinder binder = new DataBinder(order);

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
            HttpHeaders headers = new HttpHeaders();
            headers.add("message",e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getGlobalError());
        }

        orderRepo.save(order);
        Resource<PurchaseOrderDTO> returnDTO=purchaseOrderAssembler.toResource(order);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Location", returnDTO.getRequiredLink("self").getHref());


        return new ResponseEntity<Resource<PurchaseOrderDTO>>(
                returnDTO,
                headers,
                HttpStatus.OK);
    }

    public Resources<?> findAllPurchaseOrders(){
        return purchaseOrderAssembler.toResources(orderRepo.findAll());
    }


    public ResponseEntity createPO(PurchaseOrderDTO purchaseOrderDTO)
    {
        PlantInventoryEntry plantInventoryEntry;
        try {
            plantInventoryEntry = plantRepo.getOne(purchaseOrderDTO.getPlant().getContent().get_id());
            System.out.println(plantInventoryEntry);
        }
        catch (Exception e)
        {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Plant not found bad Identifier!");
        }
        PurchaseOrder po = PurchaseOrder.of(
                identifierFactory.nextPOID(),
                plantInventoryEntry,
                BusinessPeriod.of(
                        purchaseOrderDTO.getRentalPeriod().getStartDate(),
                        purchaseOrderDTO.getRentalPeriod().getEndDate()
                        ),
                purchaseOrderDTO.getAcceptHref(),
                purchaseOrderDTO.getRejectHref(),
                purchaseOrderDTO.getConsumerURI()
                );

        DataBinder binder = new DataBinder(po);

        binder.addValidators(new PurchaseOrderValidator(
                new BusinessPeriodValidator(),
                new BusinessPeriodIsInFutureValidator(),
                new PlantInventoryEntryValidator()));

        binder.validate();


        try{
            if(po.getConsumerURI()==null)
                throw new Exception();  // throw exception if consumerURI is null

        }catch (Exception e)
        {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Bad Request: Consumer URI is required!");
        }

        try {
            if (binder.getBindingResult().hasErrors()) {
                throw new BindException(binder.getBindingResult());
                }
        }
        catch (BindException e)
        {
            HttpHeaders headers = new HttpHeaders();
            headers.add("message",e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getGlobalError());
        }

        orderRepo.save(po);
        Resource<PurchaseOrderDTO> returnDTO=purchaseOrderAssembler.toResource(po);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Location", returnDTO.getRequiredLink("self").getHref());



        return new ResponseEntity<Resource<PurchaseOrderDTO>>(
                returnDTO,
                headers,
                HttpStatus.CREATED);

    }


    public ResponseEntity rejectPurchaseOrder(Long oid) {
        PurchaseOrder po;
        try {
            po = orderRepo.findPurchaseOrderById(oid);
            System.out.println(po);
        }
        catch (Exception e)
        {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getLocalizedMessage());
        }

        po.handleRejection();

        orderRepo.save(po);

        Resource<PurchaseOrderDTO> purchaseOrderDTO=purchaseOrderAssembler.toResource(po);
        sendPONotication(purchaseOrderDTO);
        return new ResponseEntity<Resource<PurchaseOrderDTO>>(
                purchaseOrderDTO,
                null,
                HttpStatus.OK);
    }


    //---------------------------------------------------------------------------------------

    public ResponseEntity allocatePlantToPurchaseOrder(Long id){
        PurchaseOrder order;


        try {
            order = orderRepo.getOne(id);
            System.out.println(order);
        }
        catch (Exception e)
        {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("PO not found bad Identifier!");
        }


        LocalDate startDate = order.getRentalPeriod().getStartDate();
        LocalDate endDate = order.getRentalPeriod().getEndDate();
        List<PlantInventoryItem> items = inventoryRepository.findAvailableItems(order.getPlant(), startDate, endDate);

        if(!items.isEmpty()){
            PlantReservation reservation = new PlantReservation();
            reservation.setId(ReservationFactory.nextReservation());
            reservation.setPlant(items.get(0));
            reservation.setSchedule(BusinessPeriod.of(startDate, endDate));
            reservationRepo.save(reservation);

            order.registerFirstAllocation(reservation);

        }
        else{
            order.handleRejection();
        }
        orderRepo.save(order);

        Resource<PurchaseOrderDTO> purchaseOrderDTO=purchaseOrderAssembler.toResource(order);
        sendPONotication(purchaseOrderDTO);



        return new ResponseEntity<Resource<PurchaseOrderDTO>>(
                purchaseOrderDTO,
                null,
                HttpStatus.OK);
    }



    public ResponseEntity requestPurchaseExtension(Long id, LocalDate endDate) {
        PurchaseOrder order;

        try {
            order = orderRepo.getOne(id);
            System.out.println(order);
        }
        catch (Exception e)
        {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("PO not found bad Identifier!");
        }


        if(order.getStatus()!=POStatus.PENDING_EXTENSION ||
                order.getStatus()!=POStatus.CLOSED ||
                order.getStatus()!=POStatus.REJECTED ||
                order.getStatus()!=POStatus.REJECTED_BY_CUSTOMER)
        order.requestExtension(endDate);
        orderRepo.save(order);


        Resource<PurchaseOrderDTO> purchaseOrderDTO=purchaseOrderAssembler.toResource(order);


        return new ResponseEntity<Resource<PurchaseOrderDTO>>(
                purchaseOrderDTO,
                null,
                HttpStatus.OK);
    }

    public ResponseEntity acceptPurchaseExtension(Long id) {

        PurchaseOrder order;

        try {
            order = orderRepo.getOne(id);
            System.out.println(order);
        }
        catch (Exception e)
        {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("PO not found bad Identifier!");
        }


        PlantInventoryItem item = order.getReservations().get(0).getPlant();

        if(inventoryRepository.isAvailableFor(item,order.getRentalPeriod().getEndDate().plusDays(1),order.pendingExtensionEndDate()))
        {
            PlantReservation plantReservation = new PlantReservation();
            plantReservation.setId(ReservationFactory.nextReservation());
            plantReservation.setPlant(item);
            plantReservation.setSchedule(BusinessPeriod.of(order.getRentalPeriod().getEndDate().plusDays(1), order.pendingExtensionEndDate()));
            reservationRepo.save(plantReservation);

            order.acceptExtension(plantReservation);
            orderRepo.save(order);
        }
        else {
//            Item not available in these dates checking for replacement....
            LocalDate startDate = order.getRentalPeriod().getStartDate();
            LocalDate endDate = order.getRentalPeriod().getEndDate();
            List<PlantInventoryItem> items = inventoryRepository.findReplacementItems(order.getPlant().getName(), startDate, endDate);

            if(!items.isEmpty()){

                if(item.getPlantInfo().getPrice().compareTo(items.get(0).getPlantInfo().getPrice())<0) {


                    BigDecimal reducedPrice = (items.get(0).getPlantInfo().getPrice()
                            .subtract(item.getPlantInfo().getPrice()));
                    float decimalval = reducedPrice.floatValue();
                    float prevPlantValue=item.getPlantInfo().getPrice().floatValue();
                    decimalval = decimalval/prevPlantValue;
                    decimalval = decimalval*100.0f;
                    if (decimalval<=30) {

                        PlantReservation plantReservation = new PlantReservation();
                        plantReservation.setId(ReservationFactory.nextReservation());
                        plantReservation.setPlant(item);
                        plantReservation.setSchedule(BusinessPeriod.of(order.getRentalPeriod().getEndDate().plusDays(1), order.pendingExtensionEndDate()));
                        reservationRepo.save(plantReservation);

                        order.acceptExtension(plantReservation);
                        orderRepo.save(order);
                    } else {

//                        Replacement not found for loss less than 30%....
                    }
                }
                else
                {
//                    Replacement found with no loss ...
                    PlantReservation plantReservation = new PlantReservation();
                    plantReservation.setId(ReservationFactory.nextReservation());
                    plantReservation.setPlant(item);
                    plantReservation.setSchedule(BusinessPeriod.of(order.getRentalPeriod().getEndDate().plusDays(1), order.pendingExtensionEndDate()));
                    reservationRepo.save(plantReservation);

                    order.acceptExtension(plantReservation);
                    orderRepo.save(order);
                }

            }
            else{
//                Replacement not found at all....
                System.out.println("Replacement not found at all....");
            }

        }

        Resource<PurchaseOrderDTO> purchaseOrderDTO=purchaseOrderAssembler.toResource(order);
        sendPONotication(purchaseOrderDTO);
        return new ResponseEntity<Resource<PurchaseOrderDTO>>(
                purchaseOrderDTO,
                null,
                HttpStatus.OK);
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

        Resource<PurchaseOrderDTO> purchaseOrderDTO=purchaseOrderAssembler.toResource(po);
        sendPONotication(purchaseOrderDTO);
        return purchaseOrderDTO;
    }

    public Resources<?> fetchPurchaseOrderExtensions(Long oid) {
        PurchaseOrder po = orderRepo.findPurchaseOrderById(oid);
        System.out.println("Printing Extensions"+po.getExtensions());
        return poExtensionAssembler.toResources(po.getExtensions(), po);
    }



    // ------------------------ Project Methods here -------------

    public ResponseEntity cancelPO(Long id) throws PurchaseOrderNotFoundException, BindException {
        PurchaseOrder purchaseOrder;
        try{
            purchaseOrder = orderRepo.getOne(id);
            System.out.println(purchaseOrder);
        }
        catch (Exception e)
        {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("PO not found bad Identifier!");
        }
        if(purchaseOrder == null) throw new PurchaseOrderNotFoundException(id);

        if(purchaseOrder.getStatus() == POStatus.OPEN ||
                purchaseOrder.getStatus() == POStatus.PENDING){
            purchaseOrder.setStatus(POStatus.CANCELLED);
            orderRepo.save(purchaseOrder);

            Resource<PurchaseOrderDTO> purchaseOrderDTO=purchaseOrderAssembler.toResource(purchaseOrder);
            sendPONotication(purchaseOrderDTO);

            return new ResponseEntity<Resource<PurchaseOrderDTO>>(
                    purchaseOrderDTO,
                    null,
                    HttpStatus.OK);
        }
        Resource<PurchaseOrderDTO> purchaseOrderDTO=purchaseOrderAssembler.toResource(purchaseOrder);
        sendPONotication(purchaseOrderDTO);
        return new ResponseEntity<Resource<PurchaseOrderDTO>>(
                purchaseOrderDTO,
                null,
                HttpStatus.OK);
    }



    public Resource<PurchaseOrderDTO> distpatchPO(Long id) throws PurchaseOrderNotFoundException {
        PurchaseOrder purchaseOrder = orderRepo.getOne(id);
        if(purchaseOrder == null) throw new PurchaseOrderNotFoundException(id);

        // Change status only if PO is in ACCEPTED state, otherwise, for now, send the PO back as it is.

        if(purchaseOrder.getStatus() == POStatus.OPEN ){
            purchaseOrder.setStatus(POStatus.DISPATCHED);
            orderRepo.save(purchaseOrder);
        }

        Resource<PurchaseOrderDTO> purchaseOrderDTO=purchaseOrderAssembler.toResource(purchaseOrder);
        sendPONotication(purchaseOrderDTO);
        return purchaseOrderDTO;
    }

    public Resource<PurchaseOrderDTO> deliverPO(Long id) throws PurchaseOrderNotFoundException {
        PurchaseOrder purchaseOrder = orderRepo.getOne(id);
        if(purchaseOrder == null) throw new PurchaseOrderNotFoundException(id);

        // if PO is in ACCEPTED or DISPATCHED state, otherwise, for now, send the PO back as it is.

        if(purchaseOrder.getStatus() == POStatus.DISPATCHED || purchaseOrder.getStatus() == POStatus.OPEN){
            purchaseOrder.setStatus(POStatus.DELIVERED);
            orderRepo.save(purchaseOrder);
        }

        Resource<PurchaseOrderDTO> purchaseOrderDTO=purchaseOrderAssembler.toResource(purchaseOrder);
        sendPONotication(purchaseOrderDTO);
        return purchaseOrderDTO;
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

            invoiceService.sendInvoice(purchaseOrderAssembler.toResource(purchaseOrder));


            purchaseOrder.setStatus(POStatus.RETURNED);
            orderRepo.save(purchaseOrder);

        }

        Resource<PurchaseOrderDTO> purchaseOrderDTO=purchaseOrderAssembler.toResource(purchaseOrder);
        sendPONotication(purchaseOrderDTO);
        return purchaseOrderDTO;
    }


    public Resources<?> findPlantsToDispatch(LocalDate givenDate){
        return plantInventoryItemAssembler.toResources(orderRepo.findPlantsToDispatch(givenDate));
    }




    //--------------- domain Notifications ------------------------


    public void sendPONotication(Resource<PurchaseOrderDTO> purchaseOrderDTO) {

        // Removing Links for consumer

        PurchaseOrderDTO tempDto= new PurchaseOrderDTO();
        tempDto.set_id(purchaseOrderDTO.getContent().get_id());
        tempDto.setRentalPeriod(purchaseOrderDTO.getContent().getRentalPeriod());
        tempDto.setPlant(purchaseOrderDTO.getContent().getPlant());
        tempDto.setStatus(purchaseOrderDTO.getContent().getStatus());
        tempDto.setTotal(purchaseOrderDTO.getContent().getTotal());
        tempDto.setConsumerURI(purchaseOrderDTO.getContent().getConsumerURI());

        Resource<PurchaseOrderDTO> stripedDTO;
        if(purchaseOrderDTO.hasLink("self")) {
            stripedDTO = new Resource(tempDto, purchaseOrderDTO.getLink("self").get());
        }
        else
        {
            stripedDTO = new Resource(tempDto);
        }

        //todo check below one is not required

        sendPONotificationByMail(stripedDTO);

    }

    public void sendPONotificationByMail(Resource<PurchaseOrderDTO> purchaseOrderDTO)
    {

        String MAIL_SUBJECT = "PO Status [Update] ";
        final String MAIL_TEXT = "Dear customer,\n\nThe status following Purchase Order No:"+purchaseOrderDTO.getContent().get_id()
                +" has been updated to : "+purchaseOrderDTO.getContent().getStatus().toString().toUpperCase()+
                "\n\nKindly yours,\n\nRentIt Team!";
        JavaMailSender mailSender = new JavaMailSenderImpl();

        String destinationEmail = customerRepository.findByConsumerURI(purchaseOrderDTO.getContent().getConsumerURI()).get(0).getEmailAddress();


        System.out.println("Sending Notification to ---------> : "+destinationEmail);


        String poDTO;
        try {
            poDTO = mapper.writeValueAsString(purchaseOrderDTO);
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
            poDTO="{}";
        }
        MimeMessage rootMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(rootMessage, true);
            helper.setFrom(emailFrom);
            helper.setTo(destinationEmail);
            helper.setSubject(MAIL_SUBJECT + purchaseOrderDTO.getContent().get_id());
            helper.setText(MAIL_TEXT);

            String filename = "po-update-"+purchaseOrderDTO.getContent().get_id()+".json";

            helper.addAttachment(filename, new ByteArrayDataSource(poDTO, "application/json"));
        } catch (MessagingException | IOException m) {
            m.printStackTrace();
        }

        poGateway.sendNotification(rootMessage);
    }

}
