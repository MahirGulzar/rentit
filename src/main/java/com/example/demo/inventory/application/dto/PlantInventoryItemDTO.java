package com.example.demo.inventory.application.dto;


import com.example.demo.inventory.domain.model.EquipmentCondition;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import lombok.Data;
import org.springframework.hateoas.ResourceSupport;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

@Data
public class PlantInventoryItemDTO extends ResourceSupport {
    Long _id;

    String serialNumber;
    @Enumerated(EnumType.STRING)
    EquipmentCondition equipmentCondition;

    @ManyToOne
    PlantInventoryEntryDTO plantInfo;
}
