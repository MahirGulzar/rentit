package com.example.demo.inventory.domain.repository;



import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

public class InventoryRepositoryImpl implements CustomInventoryRepository {
    @Autowired
    EntityManager em;

    @Override
    public List<PlantInventoryEntry> findAvailablePlants(String name, LocalDate startDate, LocalDate endDate) {
        return em.createQuery("select i.plantInfo from PlantInventoryItem i where lower(i.plantInfo.name) like concat('%', ?1, '%') and i not in " +
                        "(select r.plant from PlantReservation r where ?2 < r.schedule.endDate and ?3 > r.schedule.startDate)"
                , PlantInventoryEntry.class)
                .setParameter(1, name.toLowerCase())
                .setParameter(2, startDate)
                .setParameter(3, endDate)
                .getResultList();
    }

    @Override
    public List<PlantInventoryItem> findAvailableItems(PlantInventoryEntry entry, LocalDate startDate, LocalDate endDate) {
        return em.createQuery("select i from PlantInventoryItem i where i.plantInfo = ?1 and i not in " +
                        "(select r.plant from PlantReservation r where ?2 < r.schedule.endDate and ?3 > r.schedule.startDate)"
                , PlantInventoryItem.class)
                .setParameter(1, entry)
                .setParameter(2, startDate)
                .setParameter(3, endDate)
                .getResultList();
    }

    @Override
    public Boolean isAvailableFor(PlantInventoryItem item, LocalDate startDate, LocalDate endDate) {
        return em.createQuery("select case when (count(i) > 0)  then true else false end from PlantInventoryItem i where i = ?1 and i not in " +
                        "(select r.plant from PlantReservation r where ?2 < r.schedule.endDate and ?3 > r.schedule.startDate)"
                , Boolean.class)
                .setParameter(1, item)
                .setParameter(2, startDate)
                .setParameter(3, endDate)
                .getSingleResult();
    }
}