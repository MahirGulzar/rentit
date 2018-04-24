package com.example.demo.inventory.application.services;


import com.example.demo.inventory.application.dto.PlantInventoryItemDTO;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.inventory.rest.controllers.PlantInventoryRestController;
import com.example.demo.sales.rest.controllers.SalesRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.core.DummyInvocationUtils.methodOn;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Service
public class PlantInventoryItemAssembler {

//    public Resource<PlantInventoryItemDTO> toResource(PlantInventoryItem plant) {
//        PlantInventoryItemDTO dto = new PlantInventoryItemDTO();
//        dto.set_id(plant.getId());
//        return new Resource<>(
//                dto,
//                linkTo(methodOn(PlantInventoryRestController.class).findPlantInventoryItem(plant.getId())).withSelfRel()
//        );
//    }
//
//    public Resources<Resource<PlantInventoryItemDTO>> toResources(List<PlantInventoryItem> items) {
//        return new Resources<>(
//                items.stream().map(i -> toResource(i)).collect(Collectors.toList())
//        );
//    }
}
