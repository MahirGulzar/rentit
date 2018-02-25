package com.example.demo.repositories;

import com.example.demo.utils.Pair;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

public class MaintenancePlanRepositoryImpl implements CustomMaintenancePlanRepository {
    EntityManager em;
    @Override
    public List<Pair<Integer, Long>> findCorrectiveRepairsByYearForPeriod() {
        Query query = em.createQuery("select new com.example.demo.utils.Pair(mp.yearOfAction,count(mp))" +
                "from MaintenancePlan mp");

//        TypedQuery<Pair> query =  em.createQuery("select com.example.demo.utils.Pair(mp.yearOfAction,size(mp))" +
//                " from MaintenancePlan mp")
        return (List<Pair<Integer,Long>>)query.getResultList();
    }
}
