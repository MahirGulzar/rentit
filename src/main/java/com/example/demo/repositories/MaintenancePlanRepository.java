package com.example.demo.repositories;

import com.example.demo.models.MaintenancePlan;
import com.example.demo.utils.Pair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenancePlanRepository extends JpaRepository<MaintenancePlan,Long>,CustomMaintenancePlanRepository {

    /*@Query("select new com.example.demo.utils.Pair<f,s>() y.yearOfAction, count(y.) from MaintenancePlan y where y >= year and y <= thisyear group by y order by count(y) desc")

    List<Pair<Integer, Long>> findCorrectiveRepairsByYearForPeriod(int year, int thisyear);*/


}
