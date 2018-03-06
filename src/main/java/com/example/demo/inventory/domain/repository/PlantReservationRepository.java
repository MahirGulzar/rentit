package com.example.demo.inventory.domain.repository;


import com.example.demo.inventory.domain.model.PlantReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PlantReservationRepository extends JpaRepository<PlantReservation, Long> {

}

