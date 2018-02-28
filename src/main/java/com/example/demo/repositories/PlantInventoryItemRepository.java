package com.example.demo.repositories;

import com.example.demo.models.PlantInventoryEntry;
import com.example.demo.models.PlantInventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface PlantInventoryItemRepository extends JpaRepository<PlantInventoryItem, Long> {


//    @Query("select plt from PlantInventoryItem plt left join " +
//            "PlantReservation pr on pr.plant where pr.schedule.startDate and pr.schedule.endDate <?1")
//    List<PlantInventoryItem> findPlantsNotHiredForPeriod(LocalDate startData,LocalDate endDate);



    //-----------------------------------------------------------------------------------------

    @Query("SELECT plt FROM PlantInventoryItem plt WHERE plt NOT IN " +
            "(SELECT pr.plant FROM PlantReservation pr WHERE pr.schedule.startDate BETWEEN ?1 AND ?2 " +
            "AND pr.schedule.endDate BETWEEN ?1 AND ?2 )")
    List<PlantInventoryItem> findPlantsNotHiredForPeriod(LocalDate startData,LocalDate endDate);

    List<PlantInventoryItem> findByPlantInfo(PlantInventoryEntry entry);

    //-----------------------------------------------------------------------------------------


//    @Query("select pr.plant from PlantReservation pr where pr.schedule.startDate not between ?1 and ?2")
//    List<PlantInventoryItem> findPlantsNotHiredForPeriod(LocalDate startData,LocalDate endDate);



}
