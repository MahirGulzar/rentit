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
    @Enumerated(EnumType.STRING)
    EquipmentCondition equipmentCondition;

    @OneToOne
    PlantInventoryEntry plantInfo;

}
