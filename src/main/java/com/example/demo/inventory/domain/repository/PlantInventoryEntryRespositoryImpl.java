package com.example.demo.inventory.domain.repository;

import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDate;
import java.util.List;

public class PlantInventoryEntryRespositoryImpl implements CustomPlantInventoryEntryRepository {

    @Autowired
    EntityManager em;

    @Override
    public List<PlantInventoryEntry> findByComplicated(String name, LocalDate startDate, LocalDate endDate) {
        return em.createQuery("select i.plantInfo from PlantInventoryItem i where lower(i.plantInfo.name) like concat('%', ?1, '%') and i not in " +
                        "(select r.plant from PlantReservation r where ?2 < r.schedule.endDate and ?3 > r.schedule.startDate)"
                , PlantInventoryEntry.class)
                .setParameter(1, name.toLowerCase())
                .setParameter(2, startDate)
                .setParameter(3, endDate)
                .getResultList();
    }


//    @Override
//    public List<PlantInventoryEntry> findAvailablePlantsByDate(String name) {
//
//        Query query = em.createQuery("select plant from PlantInventoryEntry as plant");
////                               .setParameter("year",year);
//
//
////                               " from MaintenancePlan mp",Pair.class)
////        return (List<Pair<Integer,Long>>)query.getResultList();
//        return (List<PlantInventoryEntry>)query.getResultList();




}
