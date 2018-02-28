package com.example.demo.models;


import com.example.demo.models.enums.EquipmentCondition;
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
