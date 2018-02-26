package com.example.demo.repositories;

import com.example.demo.models.MaintenancePlan;
import com.example.demo.utils.Pair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaintenancePlanRepository extends JpaRepository<MaintenancePlan,Long>, CustomMaintenancePlanRepository {

    @Query("SELECT new com.example.demo.utils.Pair(mp.yearOfAction,count(corr)) from MaintenancePlan mp,MaintenanceTask corr " +
            "WHERE corr MEMBER OF mp.tasks AND " +
            "corr.typeOfWork = com.example.demo.models.enums.TypeOfWork.CORRECTIVE AND " +
            "mp.yearOfAction >=?1 AND mp.yearOfAction <=?2 group by mp.yearOfAction")
    List<Pair<Integer,Long>> findCorrectiveRepairsByYearForPeriod(int year, int thisyear);
}
