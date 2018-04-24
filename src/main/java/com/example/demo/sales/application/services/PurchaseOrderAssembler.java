package com.example.demo.sales.application.services;


import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.common.utils.ExtendedLink;
import com.example.demo.inventory.application.services.PlantInventoryEntryAssembler;
import com.example.demo.inventory.rest.controllers.PlantInventoryRestController;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.domain.model.POStatus;
import com.example.demo.sales.domain.model.PurchaseOrder;
import com.example.demo.sales.rest.controllers.SalesRestController;
import org.eclipse.jetty.http.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.afford;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.HttpMethod.DELETE;

@Service
public class PurchaseOrderAssembler {
    @Autowired
    PlantInventoryEntryAssembler plantInventoryEntryAssembler;


    public Resource<PurchaseOrderDTO> toResource(PurchaseOrder po) {
        PurchaseOrderDTO dto = new PurchaseOrderDTO();
        dto.set_id(po.getId());
        if(po.getPlant() != null) {
            dto.setPlant(plantInventoryEntryAssembler.toResource(po.getPlant()));
        }
        dto.setRentalPeriod(BusinessPeriodDTO.of(po.getRentalPeriod().getStartDate(), po.getRentalPeriod().getEndDate()));
        dto.setStatus(po.getStatus());
        dto.setTotal(po.getTotal());

        return new Resource<>(
                dto,
                linkFor(po)

        );
    }
    public Resources<Resource<PurchaseOrderDTO>> toResources(List<PurchaseOrder> orders){
        return new Resources<>(orders.stream().map(o -> toResource(o)).collect(Collectors.toList()),
                linkTo(methodOn(SalesRestController.class).findPurchaseOrders()).withSelfRel()
                        .andAffordance(afford(methodOn(SalesRestController.class).createPurchaseOrder(null)))

        );
    }

    private List<Link> linkFor(PurchaseOrder po) {
        switch (po.getStatus())
        {
            case PENDING:
                return Arrays.asList(
                        linkTo(methodOn(SalesRestController.class).fetchPurchaseOrder(po.getId())).withSelfRel(),
                        new ExtendedLink(linkTo(methodOn(SalesRestController.class).allocatePlant(po.getId())).toString(), "accept", HttpMethod.PUT),
                        new ExtendedLink(linkTo(methodOn(SalesRestController.class).rejectPurchaseOrder(po.getId())).toString(), "reject", HttpMethod.DELETE)
                );
            case OPEN:
                return  Arrays.asList(
                        linkTo(methodOn(SalesRestController.class).fetchPurchaseOrder(po.getId())).withSelfRel(),
                        new ExtendedLink(linkTo(methodOn(SalesRestController.class).handleDeleteOnPurchaseOrder(po.getId())).toString(), "close", HttpMethod.DELETE),
                        linkTo(methodOn(SalesRestController.class).retrievePurchaseOrderExtensions(po.getId())).withRel("extensions")
                                .andAffordance(afford(methodOn(SalesRestController.class).requestPurchaseOrderExtension(null, po.getId())))
                );
            case PENDING_EXTENSION:
                return Arrays.asList(
                        linkTo(methodOn(SalesRestController.class).fetchPurchaseOrder(po.getId())).withSelfRel(),
                        linkTo(methodOn(SalesRestController.class).retrievePurchaseOrderExtensions(po.getId())).withRel("extensions")
                );
            case REJECTED:
                return Arrays.asList(
                        linkTo(methodOn(SalesRestController.class).fetchPurchaseOrder(po.getId())).withSelfRel()
                                .andAffordance(afford(methodOn(SalesRestController.class).resubmitPurchaseOrder(po.getId(), null)))
                );
            case CLOSED:
                return Arrays.asList(
                        linkTo(methodOn(SalesRestController.class).fetchPurchaseOrder(po.getId())).withSelfRel()
                );
        }
        return Collections.emptyList();
    }


}