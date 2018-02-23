package com.example.demo.repository;

import com.example.demo.models.PlantInventoryEntry;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;


    public class InventoryRepositoryImpl implements CustomInventoryRepository {
        @Autowired
        EntityManager em;

        public List<PlantInventoryEntry> findAvailablePlants(String name, LocalDate startDate, LocalDate endDate) {
            return em.createQuery("select p from PlantInventoryEntry where LOWER(p.name) like ?1", PlantInventoryEntry.class)
                    .setParameter(1, name)
                    .getResultList();
        }
    }
