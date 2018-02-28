package com.example.demo.repositories;


import com.example.demo.DemoApplication;
import com.example.demo.models.*;
import com.example.demo.models.enums.TypeOfWork;
import com.example.demo.models.valueobject.BusinessPeriod;
import com.example.demo.models.valueobject.Money;
import com.example.demo.utils.Pair;
import com.example.demo.utils.YearlyRentalData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoApplication.class)
@Sql(scripts="/homework1-query-dataset.sql")
@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class YearlyRentalsTest {

    @Autowired
    PlantInventoryEntryRepository plantInventoryEntryRepository;
    @Autowired
    PlantInventoryItemRepository plantInventoryItemRepository;
    @Autowired
    PlantReservationRepository plantReservationRepository;
    @Autowired
    MaintenancePlanRepository maintenancePlanRepository;


    private void createMaintenanceTaskForYear(int year, TypeOfWork typeOfWork, BigDecimal price, PlantInventoryItem item) {
        MaintenancePlan plan = new MaintenancePlan();
        plan.setYearOfAction(year);
        plan.setPlant(item);
        MaintenanceTask task = new MaintenanceTask();
        plan.getTasks().add(task);
        task.setTypeOfWork(typeOfWork);
        task.setPrice(Money.of(price));         // Changed Big-Decimal to Money
        maintenancePlanRepository.save(plan);
    }



    @Test
    public void yearlyRentals() {
        int thisYear = LocalDate.now().getYear();
        Random random = new Random();

        int from = thisYear - 3;
        int to = thisYear-2;

        // We add a random number of maintenance tasks per year in a fixed period
        // Some of the maintenance tasks are corrective and others are preventive
        for (int year = from; year <= to; year++) {

            for(PlantInventoryItem item: plantInventoryItemRepository.findAll())
            {
                int correctiveTasks = 10;
                for (int task = 0; task < correctiveTasks; task++)
                    createMaintenanceTaskForYear(year, TypeOfWork.CORRECTIVE, null, item);
            }

        }


        //Initial Checks

        // Check 14 items in PlantInventoryItemRepository
        assertThat(plantInventoryItemRepository.findAll()).hasSize(14);
        // Check 14 entries in PlantInventoryEntryRepository
        assertThat(plantInventoryEntryRepository.findAll()).hasSize(14);
        // Check 22 reservations in PlantReservationRepository
        assertThat(plantReservationRepository.findAll()).hasSize(22);

        // Check 2 years maintenance Data
        List<Pair<Integer,Long>> yearlyPlans = maintenancePlanRepository.findCorrectiveRepairsByYearForPeriod(thisYear - 4, thisYear);
        assertThat(yearlyPlans)
                .hasSize(2);
        assertThat(yearlyPlans.get(0).getSecond().equals(new Long(140)));
        assertThat(yearlyPlans.get(1).getSecond().equals(new Long(140)));


        // test


       // List<YearlyRentalData> extensiveUsedPlant = plantInventoryEntryRepository.getExtensivelyUsedPlant(from, to);

/*
        for (PlantsWithCount item: availableExcavators) {
            System.out.println(item.getEntry().getName()+ " +++ " + item.getCount());
        }
*/




    }
}
