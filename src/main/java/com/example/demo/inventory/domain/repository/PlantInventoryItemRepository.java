package com.example.demo.inventory.domain.repository;


import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

//
//@Repository
//public interface PlantInventoryItemRepository extends JpaRepository<PlantInventoryItem, Long>,CustomInventoryRepository{
//
////
//////    @Query("select plt from PlantInventoryItem plt left join " +
//////            "PlantReservation pr on pr.plant where pr.schedule.startDate and pr.schedule.endDate <?1")
//////    List<PlantInventoryItem> findPlantsNotHiredForPeriod(LocalDate startData,LocalDate endDate);
////
////
////
////    //-----------------------------------------------------------------------------------------
////
////    @Query("SELECT plt FROM PlantInventoryItem plt WHERE plt NOT IN " +
////            "(SELECT pr.plant FROM PlantReservation pr WHERE pr.schedule.startDate BETWEEN ?1 AND ?2 " +
////            "AND pr.schedule.endDate BETWEEN ?1 AND ?2 )")
////    List<PlantInventoryItem> findPlantsNotHiredForPeriod(LocalDate startData,LocalDate endDate);
////
////    List<PlantInventoryItem> findByPlantInfo(PlantInventoryEntry entry);
////
////    //-----------------------------------------------------------------------------------------
////
////
////
////    @Query("select i from PlantInventoryItem i where i.plantInfo.id = ?1 and i not in (" +
////            "select r.plant from PlantReservation r where ?2 < r.schedule.endDate and ?3 > r.schedule.startDate)")
////    List<PlantInventoryItem> findPlantsByEntriesAndSchedule(Long id,LocalDate startDate,LocalDate endDate);
////
////
////
////    List<PlantInventoryItem> findPlantInventoryItemById(Long id);
//
//
//
//
//}


@Repository
public interface PlantInventoryItemRepository extends JpaRepository<PlantInventoryItem, Long> {
    PlantInventoryItem findOneByPlantInfo(PlantInventoryEntry entry);
}

