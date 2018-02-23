package com.example.demo.models;

import com.example.demo.enems.TypeOfWork;
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

    @OneToOne(cascade={CascadeType.ALL})
    MaintenancePlan maintenancePlan;

    String description;
    TypeOfWork typeOfWork;
    LocalDate startDate;
    LocalDate endDate;
    //Money price;
}