package com.example.demo.models;


import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class MaintenancePlan {
    @Id
    @GeneratedValue
    Long id;

    @OneToMany(cascade={CascadeType.ALL})
    List<MaintenanceTask> maintenanceTaskList;

    LocalDate yearOfAction;
}