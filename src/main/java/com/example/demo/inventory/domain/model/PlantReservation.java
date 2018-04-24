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
    @GeneratedValue
    Long id;


//
//    public static PlantReservation of(PurchaseOrder po,PlantInventoryItem plant)
//    {
//        PlantReservation pr=new PlantReservation();
//        pr.purchaseOrder=po;
//        pr.plant=plant;
//        pr.maintenancePlan=null;
//        pr.schedule = po.getRentalPeriod();
//        return pr;
//    }

    @ManyToOne
    PlantInventoryItem plant;

//    @OneToOne
//    MaintenancePlan maintenancePlan;

    @Embedded
    BusinessPeriod schedule;

}
