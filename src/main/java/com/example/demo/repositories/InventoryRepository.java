package com.example.demo.repository;

import com.example.demo.models.PlantInventoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<PlantInventoryEntry, Long> {
    //@Query(...) // To be defined!
    List<PlantInventoryEntry> findAvailablePlants(String name, LocalDate startDate, LocalDate endDate);
}