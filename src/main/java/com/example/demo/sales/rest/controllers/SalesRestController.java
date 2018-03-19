package com.example.demo.sales.rest.controllers;

import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.common.application.exceptions.PlantNotFoundException;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.dto.PlantInventoryItemDTO;
import com.example.demo.inventory.application.services.InventoryService;
import com.example.demo.inventory.application.services.PlantInventoryEntryAssembler;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.application.services.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/sales")
public class SalesRestController {



    @Autowired
    InventoryService inventoryService;

    @Autowired
    SalesService salesService;

    @GetMapping("/plants")
    public List<PlantInventoryEntryDTO> findAvailablePlants(
            @RequestParam(name = "name") String plantName,
            @RequestParam(name = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return inventoryService.findAvailable(plantName, startDate, endDate);
    }

    /*@GetMapping("/orders/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PurchaseOrderDTO fetchPurchaseOrder(@PathVariable("id") Long id) {
        return salesService.findPurchaseOrder(id);
    }*/

    @GetMapping("/orders/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PurchaseOrderDTO fetchPurchaseOrder(@PathVariable("id") Long id) {
        PurchaseOrderDTO poDTO = salesService.findPurchaseOrder(id);
        poDTO.removeLinks();
        Link selfLink = linkTo(SalesRestController.class).slash("orders").slash(poDTO.get_id()).withSelfRel();
        poDTO.add(selfLink);
        return poDTO;
    }

//    @GetMapping("/orders/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    public ResponseEntity<PurchaseOrderDTO> fetchPurchaseOrder(@PathVariable("id") Long id)throws URISyntaxException {
//        PurchaseOrderDTO poDTO=salesService.findPurchaseOrder(id);
//        HttpHeaders headers = new HttpHeaders();
//        headers.setLocation(new URI(poDTO.getId().getHref()));
//        return new ResponseEntity<>(poDTO, headers, HttpStatus.OK);
//    }

    //-------------------------------------------------------------------------------------------

    /**
     * Get PO by Status [Mahir]
     * @param status Status of Purchase Order
     * @return List of PurchaseOrderDTO's by Status
     */
    @GetMapping("/orders")
    @ResponseStatus(HttpStatus.OK)
    public List<PurchaseOrderDTO> findPurchaseOrderbyStatus(@RequestParam(name = "status") String status) {


        return salesService.findPurchaseOrderByStatus(status);
    }
//    @GetMapping("/orders")
//    @ResponseStatus(HttpStatus.OK)
//    public ResponseEntity<List<PurchaseOrderDTO>> findPurchaseOrderbyStatus(@RequestParam(name = "status") String status)throws URISyntaxException {
//        List<PurchaseOrderDTO> POs = salesService.findPurchaseOrderByStatus(status);
//        // TODO: Complete this part
//
//        HttpHeaders headers = new HttpHeaders();
////        headers.setLocation(new URI(POs.get(0).getId().getHref()));
//        for(PurchaseOrderDTO po : POs)
//        {
//            headers.setLocation(new URI(po.getId().getHref()));
//        }
//
//
//        return new ResponseEntity<List<PurchaseOrderDTO>>(POs, headers, HttpStatus.CREATED);
//    }

    /**
     * Get List of Plants (PlantInventoryItems) within the given period [Mahir]
     * @param id order id
     * @param startDate start date of rental
     * @param endDate end date of rental
     * @return List of available plants in the given period for the PO entry
     */
    @GetMapping("/orders/{oid}/plants")
    @ResponseStatus(HttpStatus.OK)
    public List<PlantInventoryItemDTO> findAvailableItems(
            @PathVariable("oid") Long id,
            @RequestParam(name = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return salesService.findAvailablePOItems(id, startDate, endDate);
    }


    /**
     * Allocate the given pid plant and get updated purchase order [Mahir]
     * @param oid purchase order Id
     * @param pid plantInventoryItem Id
     * @return updated purchase order with Status=OPEN
     */
    @PostMapping("/orders/{oid}/plants/{pid}/accept")
    @ResponseStatus(HttpStatus.OK)
    public PurchaseOrderDTO allocatePlant(
            @PathVariable("oid") Long oid,
            @PathVariable("pid") Long pid)
    {
        return salesService.allocatePlant(oid, pid);
    }



    @DeleteMapping("/orders/{oid}/accept")
    @ResponseStatus(HttpStatus.OK)
    public PurchaseOrderDTO allocatePlant(
            @PathVariable("oid") Long oid)
    {
        return salesService.rejectPurchaseOrder(oid);
    }



    //-------------------------------------------------------------------------------------------

    @ExceptionHandler(PlantNotFoundException.class)
    @PostMapping("/orders")
//    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PurchaseOrderDTO> createPurchaseOrder(@RequestBody PurchaseOrderDTO partialPODTO) throws URISyntaxException, PlantNotFoundException {

        // to Test Rest POST Kindly remove below line ...
//        partialPODTO.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(),LocalDate.now().plusDays(2)));


        PurchaseOrderDTO newlyCreatePODTO = salesService.createPO(partialPODTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(newlyCreatePODTO.getId().getHref()));

        return new ResponseEntity<>(newlyCreatePODTO, headers, HttpStatus.CREATED);
    }

    @ExceptionHandler(PlantNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handPlantNotFoundException(PlantNotFoundException ex) {
        // Code To handle Exception
    }
}