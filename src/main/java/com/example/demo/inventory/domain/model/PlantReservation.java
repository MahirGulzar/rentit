package com.example.demo.inventory.domain.model;



import com.example.demo.common.domain.model.BusinessPeriod;
import com.example.demo.maintenance.domain.model.MaintenancePlan;
import com.example.demo.sales.domain.model.PurchaseOrder;
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
    PlantInventoryItem plant;

    @OneToOne // TODO need to check
            MaintenancePlan maintenancePlan;

    @Embedded
    BusinessPeriod schedule;

}
