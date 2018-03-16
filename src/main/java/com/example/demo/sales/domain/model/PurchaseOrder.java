package com.example.demo.sales.domain.model;


import com.example.demo.common.domain.model.Address;
import com.example.demo.common.domain.model.BusinessPeriod;
import com.example.demo.common.identifiers.PurchaseOrderID;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantReservation;
import com.example.demo.common.identifiers.CustomerID;
import com.example.demo.common.identifiers.PlantInventoryEntryID;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Entity
@Getter
//@Data
@NoArgsConstructor(force = true,access = AccessLevel.PROTECTED)        // Changed from PRIVATE TO PROTECTED
public class PurchaseOrder {



    @Id
    @GeneratedValue
    Long id;


    public static PurchaseOrder of(PlantInventoryEntry plant, BusinessPeriod rentalPeriod) {
        PurchaseOrder po = new PurchaseOrder();
        po.plant = plant;
        po.rentalPeriod = rentalPeriod;
        po.status = POStatus.OPEN;

        // PO Total price


        po.total = BigDecimal.valueOf(ChronoUnit.DAYS.between(rentalPeriod.getStartDate(), rentalPeriod.getEndDate()) + 1).multiply(plant.getPrice().getPrice());

        return po;
    }

    @OneToMany
    List<PlantReservation> reservations;

    @OneToOne
    PlantInventoryEntry plant;

    LocalDate issueDate;
    LocalDate paymentSchedule;
    @Column(precision=8,scale=2)
    BigDecimal total;

    @Enumerated(EnumType.STRING)
    POStatus status;

    @Embedded
    BusinessPeriod rentalPeriod;






    // TODO will apply below changes when splitting project into micro-services




//    @NonNull
//    @Id
//    PurchaseOrderID id;        // Changed to PurchaseOrderID
//
//
//    @OneToMany
//    List<PlantReservation> reservations;    // TODO Change to ReservationsID
//
//    @NonNull
//    @OneToOne
//    PlantInventoryEntryID plantID;          // Changed from plant to plantID
//
//    @NonNull
//    @ManyToOne
//    CustomerID customerID;
//
//    @NonNull
//    @OneToOne
//    Address address;
//
//    LocalDate issueDate;
//    LocalDate paymentSchedule;
//    @Column(precision=8,scale=2)
//    BigDecimal total;
//
//    @NonNull
//    @Enumerated(EnumType.STRING)
//    POStatus status;
//
//    @NonNull
//    @Embedded
//    BusinessPeriod rentalPeriod;


//    public static PurchaseOrder of(Long id, PlantInventoryEntryID plantId, CustomerID customerID , Address address, BusinessPeriod rentalPeriod) {
//        PurchaseOrder po = new PurchaseOrder();
//        po.id = id;
//        po.plantID = plantId;
//        po.customerID = customerID;
//        po.address=address;
//        po.rentalPeriod=rentalPeriod;
//        po.status = POStatus.PENDING;
//        return po;
//
//    }

}
