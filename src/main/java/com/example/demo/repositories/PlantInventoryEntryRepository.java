package com.example.demo.repositories;

import com.example.demo.models.PlantInventoryEntry;
import com.example.demo.models.PlantInventoryItem;
import com.example.demo.models.PlantsWithCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.demo.models.enums.EquipmentCondition;

import java.time.LocalDate;
import java.util.List;

public interface PlantInventoryEntryRepository extends JpaRepository<PlantInventoryEntry,Long> {

    // To Specify by query


    List<PlantInventoryEntry> findByNameLike(String name);
    List<PlantInventoryEntry> findByNameContaining(String name);

/*
    @Query("select plt ,cond from PlantInventoryEntry plt, PlantInventoryItem cond where plt.name like '%:?1%' and cond.equipmentCondition = EquipmentCondition.SERVICEABLE ")
    List<Object[]> findAvailable(String name, LocalDate startDate, LocalDate endDate);
*/

/*    @Query("select new com.example.demo.models.PlantsWithCount(p,count(p)) from PlantInventoryItem p where p.plantInfo.name like %?1% " +
            "and p not in (select pr.plant from PlantReservation pr where ?2 < pr.schedule.endDate and ?3 > pr.schedule.startDate)")
    List<PlantInventoryItem> findAvailable(String name, LocalDate startDate, LocalDate endDate);*/


    @Query("SELECT new com.example.demo.models.PlantsWithCount(item.plantInfo,count(item.plantInfo)) FROM PlantInventoryItem item WHERE item.plantInfo.name LIKE %?1% " +
            "AND item.equipmentCondition= com.example.demo.models.enums.EquipmentCondition.SERVICEABLE " +
            "AND item NOT IN (SELECT pr.plant FROM PlantReservation pr WHERE pr.schedule.startDate BETWEEN ?2 AND ?3 " +
            "AND pr.schedule.endDate BETWEEN ?2 AND ?3) GROUP BY item.plantInfo.id")
    List<PlantsWithCount> findAvailable(String name, LocalDate startDate, LocalDate endDate); //TODO need to check other way from plant reservation



    @Query("SELECT Case when count(item) >=1 then true else false end from PlantInventoryItem item where" +
            " item not in (Select pr.plant from PlantReservation pr where pr.schedule.startDate between ?2 and ?3 " +
            "and pr.schedule.endDate between ?2 and ?3)" +
            " and item.plantInfo = ?1 and item.equipmentCondition= com.example.demo.models.enums.EquipmentCondition.SERVICEABLE")
    Boolean isThereAnyAvailableItem(PlantInventoryEntry entry, LocalDate startDate,LocalDate endDate);




}
