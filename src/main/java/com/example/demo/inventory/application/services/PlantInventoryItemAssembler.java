package com.example.demo.inventory.application.services;


import com.example.demo.inventory.application.dto.PlantInventoryItemDTO;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.inventory.rest.controllers.PlantInventoryRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;

@Service
public class PlantInventoryItemAssembler
        extends ResourceAssemblerSupport<PlantInventoryItem, PlantInventoryItemDTO> {

    public PlantInventoryItemAssembler() {
        super(PlantInventoryRestController.class, PlantInventoryItemDTO.class);
    }

    @Autowired
    public PlantInventoryEntryAssembler entryAssembler;

    @Override
    public PlantInventoryItemDTO toResource(PlantInventoryItem item) {
        PlantInventoryItemDTO dto = createResourceWithId(item.getId(), item);
        dto.set_id(item.getId());
        dto.setEquipmentCondition(item.getEquipmentCondition());
        dto.setPlantInfo(entryAssembler.toResource(item.getPlantInfo()));
        dto.setSerialNumber(item.getSerialNumber());
        return dto;
    }
}
