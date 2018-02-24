package com.example.demo.models;


import com.example.demo.models.valueobject.BusinessPeriod;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class PlantReservation {
    @Id
    @GeneratedValue
    Long id;

    @ManyToOne
    PurchaseOrder purchaseOrder;
    @OneToOne
    PlantInventoryItem plantInventoryItem;

    @OneToOne // TODO need to check
    MaintenancePlan maintenancePlan;

    @Embedded
    BusinessPeriod schedule;
}
