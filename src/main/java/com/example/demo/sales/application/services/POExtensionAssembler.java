package com.example.demo.sales.application.services;

import com.example.demo.common.utils.ExtendedLink;
import com.example.demo.sales.application.dto.POExtensionDTO;
import com.example.demo.sales.domain.model.POExtension;
import com.example.demo.sales.domain.model.PurchaseOrder;
import com.example.demo.sales.rest.controllers.SalesRestController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.afford;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


@Service
public class POExtensionAssembler {
    public Resource<POExtensionDTO> toResource(POExtension extension, PurchaseOrder order){
        POExtensionDTO dto = new POExtensionDTO();
        dto.setEndDate(extension.getEndDate());
        return new Resource<>(dto,
                linkFor(extension, order));

    }

    public Resources<?> toResources(List<POExtension> extensions, PurchaseOrder order){


        return new Resources<>(extensions.stream().map(o -> toResource(o, order)).collect(Collectors.toList()),
                linkTo(methodOn(SalesRestController.class).retrievePurchaseOrderExtensions(order.getId())).withSelfRel()
                        .andAffordance(afford(methodOn(SalesRestController.class).requestPurchaseOrderExtension(null, order.getId()))));
    }
    private List<Link> linkFor(POExtension extension, PurchaseOrder order) {

        return Arrays.asList(
                linkTo(methodOn(SalesRestController.class).retrievePurchaseOrderExtensions(order.getId())).withSelfRel()
        );
//        switch (extension.getStatus())
//        {
//            case PENDING:
//                return Arrays.asList(
//                        linkTo(methodOn(SalesRestController.class).retrievePurchaseOrderExtensions(order.getId())).withSelfRel(),
//                        new ExtendedLink(linkTo(methodOn(SalesRestController.class).acceptPurchaseOrderExtension(order.getId(), null)).toString(), "accept", HttpMethod.PATCH),
//                        new ExtendedLink(linkTo(methodOn(SalesRestController.class).rejectPurchaseOrder(order.getId())).toString(), "reject", HttpMethod.DELETE)
//                );
//            case ACCEPTED:
//                return Arrays.asList(
//                        linkTo(methodOn(SalesRestController.class).retrievePurchaseOrderExtensions(order.getId())).withSelfRel()
//                );
//            case REJECTED:
//                return Arrays.asList(
//                        linkTo(methodOn(SalesRestController.class).retrievePurchaseOrderExtensions(order.getId())).withSelfRel()
//                );
//            default:
//                return Arrays.asList(
//                        linkTo(methodOn(SalesRestController.class).retrievePurchaseOrderExtensions(order.getId())).withSelfRel()
//                );
//        }
//
    }

}
