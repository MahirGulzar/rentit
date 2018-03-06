package com.example.demo.maintenance.domain.repository;

import com.example.demo.common.utils.Pair;
import com.example.demo.maintenance.domain.model.MaintenancePlan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaintenancePlanRepository extends JpaRepository<MaintenancePlan,Long>, CustomMaintenancePlanRepository {

    @Query("SELECT new com.example.demo.common.utils.Pair(mp.yearOfAction,count(corr)) from MaintenancePlan mp,MaintenanceTask corr " +
            "WHERE corr MEMBER OF mp.tasks AND " +
            "corr.typeOfWork = com.example.demo.maintenance.domain.model.TypeOfWork.CORRECTIVE AND " +
            "mp.yearOfAction >=?1 AND mp.yearOfAction <=?2 group by mp.yearOfAction")
    List<Pair<Integer,Long>> findCorrectiveRepairsByYearForPeriod(int year, int thisyear);

    @Query("select new com.example.demo.common.utils.Pair(mp.yearOfAction,sum(mt.price.price)) from MaintenancePlan mp,MaintenanceTask mt " +
            "where mt member of mp.tasks and " +
            "mt.typeOfWork = com.example.demo.maintenance.domain.model.TypeOfWork.CORRECTIVE and " +
            "mp.yearOfAction >=?1 and mp.yearOfAction <=?2 " +
            "group by mp.yearOfAction")
    List<Pair<Integer,Long>> findCorrectiveRepairCostsByYear2(int year, int thisyear);
}

