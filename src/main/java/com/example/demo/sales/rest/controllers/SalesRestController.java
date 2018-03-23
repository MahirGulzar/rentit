package com.example.demo.sales.rest.controllers;


import com.example.demo.common.utils.ExtendedLink;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.dto.PlantInventoryItemDTO;
import com.example.demo.inventory.application.exceptions.PlantNotFoundException;
import com.example.demo.inventory.application.services.InventoryService;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.application.services.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.POST;


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
        return inventoryService.findAvailable(plantName.toLowerCase(), startDate, endDate);
    }


    @GetMapping("/orders/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PurchaseOrderDTO fetchPurchaseOrder(@PathVariable("id") Long id) {
        PurchaseOrderDTO poDTO = salesService.findPurchaseOrder(id);
        return poDTO;
    }


    //-------------------------------------------------------------------------------------------

    /**
     * Get PO by Status [Mahir]
     * @param status Status of Purchase Order
     * @return List of PurchaseOrderDTO's by Status
     */
    @GetMapping("/orders")
    @ResponseStatus(HttpStatus.OK)
    public List<PurchaseOrderDTO> findPurchaseOrderbyStatus(@RequestParam(name = "status",required = false) String status) {

        if(status!= null) {
            return salesService.findPurchaseOrderByStatus(status.toString());
        }

        return salesService.findAllPurchaseOrders();

    }

    /**
     * Get List of Plants (PlantInventoryItems) within the given period [Mahir]
     * @param id order id
     * @return List of available plants in the given period for the PO entry
     */
    @GetMapping("/orders/{oid}/plants")
    @ResponseStatus(HttpStatus.OK)
    public List<PlantInventoryItemDTO> findAvailableItems(
            @PathVariable("oid") Long id){
        List<PlantInventoryItemDTO> resources = salesService.findAvailablePOItems(id);
        for(PlantInventoryItemDTO dto : resources)
        {
            Link acceptLink = linkTo(SalesRestController.class).slash("orders").slash(id).slash("plants").slash(dto.get_id()).slash("accept").withRel("accept");
            dto.add(new ExtendedLink(acceptLink,POST));
        }

        return resources;
    }


    /**
     * Allocate the given pid plant and get updated purchase order [Mahir]
     * @param oid purchase order Id
     * @param pid plantInventoryItem Id
     * @return updated purchase order with Status=OPEN
     */
    @PostMapping("/orders/{oid}/plants/{pid}/accept")
    @ResponseStatus(HttpStatus.CREATED)
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


    @PostMapping("/orders")
    public ResponseEntity<PurchaseOrderDTO> createPurchaseOrder(@RequestBody PurchaseOrderDTO partialPODTO) throws URISyntaxException {

        HttpHeaders headers = new HttpHeaders();
        try {
            PurchaseOrderDTO newlyCreatePODTO = salesService.createPO(partialPODTO);
            headers.setLocation(new URI(newlyCreatePODTO.getId().getHref()));
            return new ResponseEntity<>(newlyCreatePODTO, headers, HttpStatus.CREATED);
        }
        catch (PlantNotFoundException e) {
            return new ResponseEntity<PurchaseOrderDTO>(e.getPurchaseOrder(), headers, HttpStatus.NOT_FOUND);
        } catch (URISyntaxException e) {
            return new ResponseEntity<PurchaseOrderDTO>(headers, HttpStatus.BAD_REQUEST);
        } catch (BindException e){
            return new ResponseEntity<PurchaseOrderDTO>(headers, HttpStatus.BAD_REQUEST);
        }


    }

}