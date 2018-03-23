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
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
//@Data
@NoArgsConstructor(force = true,access = AccessLevel.PROTECTED)        // Changed from PRIVATE TO PROTECTED
public class PurchaseOrder {



    @Id
    //@GeneratedValue
    String id;


    public static PurchaseOrder of(String id, PlantInventoryEntry plant, BusinessPeriod rentalPeriod) {
        PurchaseOrder po = new PurchaseOrder();
        po.id = id;
        po.plant = plant;
        po.rentalPeriod = rentalPeriod;
        po.status = POStatus.PENDING;
        po.total = BigDecimal.valueOf(ChronoUnit.DAYS.between(rentalPeriod.getStartDate(), rentalPeriod.getEndDate()) + 1).multiply(plant.getPrice().getPrice());
        po.reservations = new ArrayList<>();
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



    public void createReservation(PlantReservation pr)
    {
        this.reservations.add(pr);
        this.status=POStatus.OPEN;
    }

    public void handleRejection()
    {
        this.status=POStatus.REJECTED;
    }




    // TODO will apply below changes when splitting project into micro-services




}
