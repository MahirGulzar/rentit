package com.example.demo.repository;

import com.example.demo.models.PlantInventoryEntry;

import java.time.LocalDate;
import java.util.List;

public interface CustomInventoryRepository {
    List<PlantInventoryEntry> findAvailablePlants(String name, LocalDate startDate, LocalDate endDate);
}