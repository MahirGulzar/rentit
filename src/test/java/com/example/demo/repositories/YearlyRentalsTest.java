package com.example.demo.repositories;


import com.example.demo.DemoApplication;
import com.example.demo.models.MaintenancePlan;
import com.example.demo.models.MaintenanceTask;
import com.example.demo.models.PlantInventoryItem;
import com.example.demo.models.PlantReservation;
import com.example.demo.models.enums.TypeOfWork;
import com.example.demo.models.valueobject.Money;
import com.example.demo.utils.Pair;
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
    PlantInventoryItemRepository plantInventoryItemRepository;
    @Autowired
    PlantReservationRepository plantReservationRepository;



    @Autowired
    MaintenancePlanRepository maintenancePlanRepository;

    private void createMaintenanceTaskForYear(int year, TypeOfWork typeOfWork, BigDecimal price, PlantInventoryItem item) {
        MaintenancePlan plan = new MaintenancePlan();
        for(PlantReservation reservation: plantReservationRepository.findAll())
        {

        }
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
        List<Pair<Integer, Long>> expectedResult = new ArrayList<>();
        Random random = new Random();




        // We add a random number of maintenance tasks per year in a fixed period
        // Some of the maintenance tasks are corrective and others are preventive
        for (int year = thisYear - 3; year <= thisYear-2; year++) {

            for(PlantInventoryItem item: plantInventoryItemRepository.findAll())
            {
                int correctiveTasks = random.nextInt(10) + 1;
                for (int task = 0; task < correctiveTasks; task++)
                    createMaintenanceTaskForYear(year, TypeOfWork.CORRECTIVE, null, item);
            }

        }





        // We expect that the database query returns the right number of corrective tasks
        // per year (we try with the original period of years and with two more shorter periods)
//        assertThat(maintenancePlanRepository.findCorrectiveRepairsByYearForPeriod(thisYear - 3, thisYear-2))
//                .containsAll(expectedResult);

    }

}
