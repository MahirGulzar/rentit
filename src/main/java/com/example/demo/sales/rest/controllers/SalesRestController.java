package com.example.demo.sales.rest.controllers;

import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.common.application.exceptions.PlantNotFoundException;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.services.InventoryService;
import com.example.demo.inventory.application.services.PlantInventoryEntryAssembler;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.application.services.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    @GetMapping("/orders/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PurchaseOrderDTO fetchPurchaseOrder(@PathVariable("id") Long id) {
        return salesService.findPurchaseOrder(id);
    }

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