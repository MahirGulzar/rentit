package com.example.demo.maintenance.domain.model;

import com.example.demo.common.domain.model.BusinessPeriod;
import com.example.demo.common.domain.model.Money;
import com.example.demo.inventory.domain.model.PlantReservation;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
public class MaintenanceTask {
    @Id
    @GeneratedValue
    Long id;

    String description;
    @Enumerated(EnumType.STRING)
    TypeOfWork typeOfWork;
    @Embedded
    BusinessPeriod maintenancePeriod;
    @Embedded
    Money price;


    @OneToOne
    PlantReservation plantReservation;


}