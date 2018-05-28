package com.example.demo.inventory.domain.model;



import com.example.demo.common.domain.model.BusinessPeriod;
import com.example.demo.maintenance.domain.model.MaintenancePlan;
import com.example.demo.sales.domain.model.PurchaseOrder;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
public class PlantReservation {
    @Id
    Long id;

    @ManyToOne
    PlantInventoryItem plant;


    @Embedded
    BusinessPeriod schedule;

}
