package com.example.demo.inventory.domain.repository;

import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.utils.PlantsWithCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.time.LocalDate;
import java.util.List;

//@Repository
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


    @Query("SELECT new com.example.demo.inventory.utils.PlantsWithCount(item.plantInfo,count(item.plantInfo)) FROM PlantInventoryItem item WHERE item.plantInfo.name LIKE %?1% " +
            "AND item.equipmentCondition= com.example.demo.inventory.domain.model.EquipmentCondition.SERVICEABLE " +
            "AND item NOT IN (SELECT pr.plant FROM PlantReservation pr WHERE pr.schedule.startDate BETWEEN ?2 AND ?3 " +
            "AND pr.schedule.endDate BETWEEN ?2 AND ?3) GROUP BY item.plantInfo.id")
    List<PlantsWithCount> findAvailable(String name, LocalDate startDate, LocalDate endDate); //TODO need to check other way from plant reservation

    @Query("SELECT (COUNT(item) <> 0)  FROM PlantInventoryItem item WHERE" +
            " item NOT IN (SELECT pr.plant FROM PlantReservation pr WHERE pr.schedule.startDate BETWEEN ?2 AND ?3 " +
            "AND pr.schedule.endDate BETWEEN ?2 AND ?3)" +
            "AND item.plantInfo = ?1 AND item.equipmentCondition= com.example.demo.inventory.domain.model.EquipmentCondition.SERVICEABLE")
    Boolean isThereAnyAvailableItem(PlantInventoryEntry entry, LocalDate startDate,LocalDate endDate);


/*    @Query("SELECT new com.example.demo.utils.YearlyRentalData(year(pr.schedule.startDate),pr.plant.plantinfo.name,sum(pr.id),count(pr.maintenancePlan.tasks)) FROM PlantReservation pr " +
            "WHERE (EXTRACT(YEAR pr.schedule.startDate) BETWEEN ?1 and ?2  ) OR EXTRACT(WEEK pr.schedule.endDate) BETWEEN ?1 and ?2 )) " +
            "AND EXTRACT(YEAR mp.yearOfAction) BETWEEN ?1 and ?2" +
            "AND EXTRACT(YEAR mp.tasks.TypeOfWork=com.example.demo.models.enums.TypeOfWork.CORRECTIVE) " +
            "")
    List<YearlyRentalData> getExtensivelyUsedPlant(int start, int end);*/



//----------------------------------------------------------------------------------------------------------------------------------------

// Query for cucumber

    /*@Query("SELECT entr FROM PlantInventoryEntry entr, PlantInventoryItem item WHERE item.plantInfo.name LIKE %?1%" +
            "AND item.equipmentCondition= com.example.demo.inventory.domain.model.EquipmentCondition.SERVICEABLE " +
            "AND item NOT IN (SELECT pr.plant FROM PlantReservation pr WHERE pr.schedule.startDate BETWEEN ?2 AND ?3 " +
            "AND pr.schedule.endDate BETWEEN ?2 AND ?3) GROUP BY item.plantInfo.id")
    List<PlantInventoryEntry> findAvailableEntries(String name, LocalDate startDate, LocalDate endDate);
*/

    //----------------------------------------------------------------------------------------------------------------------------------------


}
