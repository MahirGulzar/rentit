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

    @Query("select plt from PlantInventoryItem plt where plt not in " +
            "(select pr.plant from PlantReservation pr where pr.schedule.startDate between ?1 and ?2 " +
            "and pr.schedule.endDate between ?1 and ?2 )")
    List<PlantInventoryItem> findPlantsNotHiredForPeriod(LocalDate startData,LocalDate endDate);

    //-----------------------------------------------------------------------------------------

    
//    @Query("select pr.plant from PlantReservation pr where pr.schedule.startDate not between ?1 and ?2")
//    List<PlantInventoryItem> findPlantsNotHiredForPeriod(LocalDate startData,LocalDate endDate);


}
