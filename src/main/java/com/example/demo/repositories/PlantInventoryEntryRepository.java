package com.example.demo.repositories;

import com.example.demo.models.PlantInventoryEntry;
import com.example.demo.models.PlantInventoryItem;
import com.example.demo.models.PlantsWithCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PlantInventoryEntryRepository extends JpaRepository<PlantInventoryEntry,Long> {

    // To Specify by query


    List<PlantInventoryEntry> findByNameLike(String name);
    List<PlantInventoryEntry> findByNameContaining(String name);

   // @Query("select new com.example.demo.models.PlantsWithCount(p,count(p)) from PlantInventoryItem p where p.plantInfo.name like %?1% " +
     //       "and p not in (select pr.plant from PlantReservation pr where ?2 < pr.schedule.endDate and ?3 > pr.schedule.startDate)")
//    List<PlantInventoryItem> findAvailable(String name, LocalDate startDate, LocalDate endDate);

    @Query("select CASE WHEN (count(p.id) > 0) THEN TRUE ELSE FALSE END from PlantInventoryItem p where p.plantInfo = ?1" +
            "and p.equipmentCondition = com.example.inventory.domain.model.EquipmentCondition.SERVICEABLE " +
            "and p not in (select pr.plant from PlantReservation pr where pr.schedule.endDate > ?2 and  pr.schedule.startDate < ?3 )")
    Boolean isThereAnyAvailableItem(PlantInventoryEntry plantInventoryEntry, LocalDate startDate, LocalDate endDate);




}
