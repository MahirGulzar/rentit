package com.example.demo.models;

import com.example.demo.models.enums.TypeOfWork;
import com.example.demo.models.valueobject.BusinessPeriod;
import com.example.demo.models.valueobject.Money;
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


//    @OneToOne
//    PlantReservation plantReservation;

    @ManyToOne //TODO we need to check this will be here or not.
    MaintenancePlan maintenancePlan;

}