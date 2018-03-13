package com.example.demo.sales.application.services;


import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.inventory.application.services.PlantInventoryEntryAssembler;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.domain.model.PurchaseOrder;
import com.example.demo.sales.rest.controllers.SalesRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;

@Service
public class PurchaseOrderAssembler extends ResourceAssemblerSupport<PurchaseOrder, PurchaseOrderDTO> {

    @Autowired
    PlantInventoryEntryAssembler plantInventoryEntryAssembler;

    public PurchaseOrderAssembler() {
        super(SalesRestController.class, PurchaseOrderDTO.class);
    }

    public PurchaseOrderDTO toResource(PurchaseOrder purchaseOrder) {
        PurchaseOrderDTO dto = createResourceWithId(purchaseOrder.getId(), purchaseOrder);
        dto.set_id(purchaseOrder.getId());
        dto.setPlant(plantInventoryEntryAssembler.toResource(purchaseOrder.getPlant()));
        dto.setRentalPeriod(BusinessPeriodDTO.of(purchaseOrder.getRentalPeriod().getStartDate(),purchaseOrder.getRentalPeriod().getEndDate()));
        dto.setTotal(purchaseOrder.getTotal());
        dto.setStatus(purchaseOrder.getStatus());
        return dto;
    }
}