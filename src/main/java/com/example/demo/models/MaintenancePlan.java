package com.example.demo.models;


import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class MaintenancePlan {
    @Id
    @GeneratedValue
    Long id;

    int yearOfAction;

    @OneToMany(cascade={CascadeType.ALL})
    List<MaintenanceTask> tasks = new ArrayList<>();

    @OneToOne
    PlantInventoryItem plant;
}