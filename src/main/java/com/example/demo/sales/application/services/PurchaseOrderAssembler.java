package com.example.demo.sales.application.services;


import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.common.utils.ExtendedLink;
import com.example.demo.inventory.application.services.PlantInventoryEntryAssembler;
import com.example.demo.inventory.rest.controllers.PlantInventoryRestController;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.domain.model.POStatus;
import com.example.demo.sales.domain.model.PurchaseOrder;
import com.example.demo.sales.rest.controllers.SalesRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Service
public class PurchaseOrderAssembler extends ResourceAssemblerSupport<PurchaseOrder, PurchaseOrderDTO> {

    @Autowired
    PlantInventoryEntryAssembler plantInventoryEntryAssembler;

    @Autowired
    SalesService salesService;

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
        dto.removeLinks();
        Link selfLink = linkTo(SalesRestController.class).slash("orders").slash(dto.get_id()).withSelfRel();
        dto.add(selfLink);

        switch (dto.getStatus())
        {
            case PENDING:
                Link rejectLink = linkTo(SalesRestController.class).slash("orders").slash(dto.get_id()).slash("accept").withRel("rejectPurchaseOrder");
                Map <String, Object> hm = new HashMap<String, Object>();
                hm.put("method","DELETE");
                rejectLink.expand(hm);
                dto.add(new ExtendedLink(rejectLink,"DELETE"));
                break;
            default:
                break;
        }

        return dto;
    }
}