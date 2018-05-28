package com.example.demo.sales.domain.model;


import com.example.demo.common.domain.model.BusinessPeriod;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantReservation;
import com.example.demo.mailing.domain.model.Customer;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Data
@NoArgsConstructor(force = true,access = AccessLevel.PROTECTED)        // Changed from PRIVATE TO PROTECTED
public class PurchaseOrder {



    @Id
    Long id;


    public static PurchaseOrder of(Long id, PlantInventoryEntry plant, BusinessPeriod rentalPeriod, String acceptHref, String rejectHref, String consumerURI) {
        PurchaseOrder po = new PurchaseOrder();
        po.id = id;
        po.plant = plant;
        po.rentalPeriod = rentalPeriod;
        po.status = POStatus.PENDING;
        po.total = BigDecimal.valueOf(ChronoUnit.DAYS.between(rentalPeriod.getStartDate(), rentalPeriod.getEndDate()) + 1).multiply(plant.getPrice());
        po.reservations = new ArrayList<>();
        po.acceptHref=acceptHref;
        po.rejectHref=rejectHref;
        po.consumerURI=consumerURI;

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

    @ManyToOne
    Customer customer;


    @ElementCollection
    List<POExtension> extensions = new ArrayList<>();

    // Added for notification on BuiltIT
    String acceptHref;
    String rejectHref;

    String consumerURI;

    public void createReservation(PlantReservation pr)
    {
        this.reservations.add(pr);
        this.status=POStatus.OPEN;
    }

    public void handleRejection()
    {
        this.status=POStatus.REJECTED;
    }


    // New Additions
    //--------------------------------------------------------------------------------------------------
    public POExtension requestExtension(LocalDate endDate){
        POExtension extension = POExtension.of(endDate, POExtension.Status.PENDING);
        extensions.add(extension);
        status =POStatus.PENDING_EXTENSION;
        return extension;
    }

    public LocalDate pendingExtensionEndDate(){
        if(extensions.size() > 0){
            POExtension openExtension = extensions.get(extensions.size() -1);
            return openExtension.getEndDate();
        }
        return null;
    }

    public boolean rejectCurrentExtension(){
        if(extensions.size() > 0){
            extensions.set(extensions.size() - 1, POExtension.of(pendingExtensionEndDate(), POExtension.Status.REJECTED));
            status =POStatus.OPEN;
            return true;
        }
        return false;
    }

    public void acceptExtension(PlantReservation reservation) {
        if (extensions.size() > 0) {
            extensions.set(extensions.size() - 1, POExtension.of(pendingExtensionEndDate(), POExtension.Status.ACCEPTED));
            reservations.add(reservation);
            status = POStatus.OPEN;
            rentalPeriod = BusinessPeriod.of(rentalPeriod.getStartDate(), reservation.getSchedule().getEndDate());
            total = total.add(plant.getPrice().multiply(new BigDecimal((ChronoUnit.DAYS.between(reservation.getSchedule().getStartDate(), reservation.getSchedule().getEndDate())) + 1)));

        }
    }


    public void registerFirstAllocation(PlantReservation reservation) {
        reservations.add(reservation);
        status = POStatus.OPEN;
        rentalPeriod = BusinessPeriod.of(reservation.getSchedule().getStartDate(), reservation.getSchedule().getEndDate());
        total = plant.getPrice().multiply(new BigDecimal(ChronoUnit.DAYS.between(rentalPeriod.getStartDate(), rentalPeriod.getEndDate())+1));
    }

    public void handleClose() {
        status = POStatus.CLOSED;
    }




}
