package com.example.demo.repositories;

import com.example.demo.utils.Pair;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomMaintenancePlanRepository {

    //@Query("select new com.example.demo.utils.Pair<Integer,Long>() from MaintenancePlan mp,mp.yearOfAction f,mp.tasks.")
    //List<Pair<Integer, Long>> findCorrectiveRepairsByYearForPeriod(int year, int thisyear);

    List<Pair<Integer, Long>> findCorrectiveRepairCostsByYear(int year, int thisyear);

}
