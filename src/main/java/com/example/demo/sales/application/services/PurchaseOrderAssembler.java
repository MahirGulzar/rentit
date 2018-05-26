package com.example.demo.sales.application.services;


import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.common.application.exceptions.PurchaseOrderNotFoundException;
import com.example.demo.common.utils.ExtendedLink;
import com.example.demo.inventory.application.services.PlantInventoryEntryAssembler;
import com.example.demo.inventory.rest.controllers.PlantInventoryRestController;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.domain.model.POStatus;
import com.example.demo.sales.domain.model.PurchaseOrder;
import com.example.demo.sales.rest.controllers.SalesRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
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
        dto.setAcceptHref(po.getAcceptHref());
        dto.setRejectHref(po.getRejectHref());
        dto.setConsumerURI(po.getConsumerURI());


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
                        new ExtendedLink(linkTo(methodOn(SalesRestController.class).allocatePlant(po.getId())).toString(), "allocate", HttpMethod.PUT),
                        new ExtendedLink(linkTo(methodOn(SalesRestController.class).rejectPurchaseOrder(po.getId())).toString(), "reject", HttpMethod.DELETE)
                );
            case OPEN:
                try {
                    return Arrays.asList(
                            linkTo(methodOn(SalesRestController.class).fetchPurchaseOrder(po.getId())).withSelfRel(),
                            new ExtendedLink(linkTo(methodOn(SalesRestController.class).handleDeleteOnPurchaseOrder(po.getId())).toString(), "close", HttpMethod.DELETE),
                            new ExtendedLink(linkTo(methodOn(SalesRestController.class).retrievePurchaseOrderExtensions(po.getId())).toString(), "show extensions", HttpMethod.GET),
                            new ExtendedLink(linkTo(methodOn(SalesRestController.class).dispatchPurchaseOrder(po.getId())).toString(), "dispatch", HttpMethod.GET),
                            new ExtendedLink(linkTo(methodOn(SalesRestController.class).requestPurchaseOrderExtension(null, po.getId())).toString(), "request extension", HttpMethod.POST)

                    );
                }
                catch (PurchaseOrderNotFoundException exception)
                {
                    // PO not found here..
                }
                catch (BindException bindException)
                {
                    // Bind exception for validator
                }
            case PENDING_EXTENSION:
                return Arrays.asList(
                        linkTo(methodOn(SalesRestController.class).fetchPurchaseOrder(po.getId())).withSelfRel(),
                        new ExtendedLink(linkTo(methodOn(SalesRestController.class).acceptPurchaseOrderExtension(po.getId())).toString(), "accept extension", HttpMethod.PATCH),
                        new ExtendedLink(linkTo(methodOn(SalesRestController.class).rejectPurchaseOrderExtension(po.getId())).toString(), "reject extension", HttpMethod.DELETE),
                        new ExtendedLink(linkTo(methodOn(SalesRestController.class).retrievePurchaseOrderExtensions(po.getId())).toString(), "show extensions", HttpMethod.GET)
                );
            case DISPATCHED:
                try {
                    return Arrays.asList(
                            linkTo(methodOn(SalesRestController.class).fetchPurchaseOrder(po.getId())).withSelfRel(),
                            new ExtendedLink(linkTo(methodOn(SalesRestController.class).retrievePurchaseOrderExtensions(po.getId())).toString(), "show extensions", HttpMethod.GET),
                            new ExtendedLink(linkTo(methodOn(SalesRestController.class).deliveredPurchaseOrder(po.getId())).toString(), "deliver", HttpMethod.GET),
                            new ExtendedLink(linkTo(methodOn(SalesRestController.class).customerRejectedPurchaseOrder(po.getId())).toString(), "reject by customer", HttpMethod.GET),
                            new ExtendedLink(linkTo(methodOn(SalesRestController.class).requestPurchaseOrderExtension(null, po.getId())).toString(), "request extension", HttpMethod.POST)

                    );
                }
                catch (PurchaseOrderNotFoundException exception)
                {
                    // PO not found here..
                }
                catch (BindException bindException)
                {
                    // Bind exception for validator
                }
            case DELIVERED:
                try {
                    return Arrays.asList(
                            linkTo(methodOn(SalesRestController.class).fetchPurchaseOrder(po.getId())).withSelfRel(),
                            new ExtendedLink(linkTo(methodOn(SalesRestController.class).retrievePurchaseOrderExtensions(po.getId())).toString(), "show extensions", HttpMethod.GET),
                            new ExtendedLink(linkTo(methodOn(SalesRestController.class).returnPurchaseOrder(po.getId())).toString(), "return", HttpMethod.GET),
                            new ExtendedLink(linkTo(methodOn(SalesRestController.class).requestPurchaseOrderExtension(null, po.getId())).toString(), "request extension", HttpMethod.POST)
                    );
                }
                catch (PurchaseOrderNotFoundException exception)
                {
                    // PO not found here..
                }
                catch (BindException bindException)
                {
                    // Bind exception for validator
                }
            case REJECTED:
                return Arrays.asList(
                        linkTo(methodOn(SalesRestController.class).fetchPurchaseOrder(po.getId())).withSelfRel()
                                .andAffordance(afford(methodOn(SalesRestController.class).resubmitPurchaseOrder(po.getId(), null)))
                );
            case REJECTED_BY_CUSTOMER:
                return Arrays.asList(
                        linkTo(methodOn(SalesRestController.class).fetchPurchaseOrder(po.getId())).withSelfRel(),
                        new ExtendedLink(linkTo(methodOn(SalesRestController.class).handleDeleteOnPurchaseOrder(po.getId())).toString(), "close", HttpMethod.DELETE)
                );
            case INVOICED:
                return Arrays.asList(
                        linkTo(methodOn(SalesRestController.class).fetchPurchaseOrder(po.getId())).withSelfRel(),
                        new ExtendedLink(linkTo(methodOn(SalesRestController.class).handleDeleteOnPurchaseOrder(po.getId())).toString(), "close", HttpMethod.DELETE)
                );


        }
        return Collections.emptyList();
    }


}