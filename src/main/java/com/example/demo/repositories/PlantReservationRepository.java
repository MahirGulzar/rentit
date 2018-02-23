package com.example.demo.repository;

import com.example.demo.models.PlantInventoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantReservationRepository extends JpaRepository<PlantInventoryEntry, Long> {}
