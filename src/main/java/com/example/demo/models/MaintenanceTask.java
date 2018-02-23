package com.example.demo.models;

import com.example.demo.models.enums.TypeOfWork;
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

    @OneToOne
    PlantReservation plantReservation;

    @OneToOne //TODO we need to check this will be here or not.
    MaintenancePlan maintenancePlan;

    String description;
    TypeOfWork typeOfWork;
    LocalDate startDate;
    LocalDate endDate;
    Money price;
}