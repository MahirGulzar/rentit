package com.example.demo.inventory.domain.model;



import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class PlantInventoryItem {
    @Id
    @GeneratedValue
    Long id;

    String serialNumber;
    @OneToOne
    PlantInventoryEntry plantInfo;

    @Enumerated(EnumType.STRING)
    EquipmentCondition equipmentCondition;



}
