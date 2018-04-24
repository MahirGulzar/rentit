package com.example.demo.inventory.application.services;


import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.rest.controllers.PlantInventoryRestController;
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
public class PlantInventoryEntryAssembler {

    public Resource<PlantInventoryEntryDTO> toResource(PlantInventoryEntry plant) {
        PlantInventoryEntryDTO dto = new PlantInventoryEntryDTO();
        dto.set_id(plant.getId());
        dto.setName(plant.getName());
        dto.setDescription(plant.getDescription());
        dto.setPrice(plant.getPrice());

        return new Resource<>(
                dto,
                linkTo(methodOn(PlantInventoryRestController.class).findPlantInventoryEntry(plant.getId()))
                        .withSelfRel()
        );
    }

    public Resources<Resource<PlantInventoryEntryDTO>> toResources(List<PlantInventoryEntry> plants) {
        return new Resources<>(
                plants.stream().map(plant -> toResource(plant)).collect(Collectors.toList())
        );
    }
}
