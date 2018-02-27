package com.example.demo.repositories;

import com.example.demo.DemoApplication;
import com.example.demo.models.MaintenancePlan;
import com.example.demo.models.MaintenanceTask;
import com.example.demo.models.PlantInventoryItem;
import com.example.demo.models.enums.TypeOfWork;
import com.example.demo.models.valueobject.Money;
import com.example.demo.repositories.MaintenancePlanRepository;
import com.example.demo.utils.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoApplication.class)
@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MaintenanceRepositoryTests {

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
    public void findCorrectiveRepairsByYear() {
        int thisYear = LocalDate.now().getYear();
        List<Pair<Integer, Long>> expectedResult = new ArrayList<>();
        Random random = new Random();

        // We add a random number of maintenance tasks per year in a fixed period
        // Some of the maintenance tasks are corrective and others are preventive
        for (int year = thisYear - 4; year <= thisYear; year++) {
            int correctiveTasks = random.nextInt(10) + 1;
            expectedResult.add(new Pair<>(year, new Long(correctiveTasks)));
            for (int task = 0; task < correctiveTasks; task++)
                createMaintenanceTaskForYear(year, TypeOfWork.CORRECTIVE, null, null);
            int additionalTasks = random.nextInt(5);
            for (int task = 0; task < additionalTasks; task++)
                createMaintenanceTaskForYear(year, TypeOfWork.PREVENTIVE, null, null);
        }

        // We expect that the database query returns the right number of corrective tasks
        // per year (we try with the original period of years and with two more shorter periods)
        assertThat(maintenancePlanRepository.findCorrectiveRepairsByYearForPeriod(thisYear - 4, thisYear))
                .containsAll(expectedResult);
        assertThat(maintenancePlanRepository.findCorrectiveRepairsByYearForPeriod(thisYear - 3, thisYear))
                .containsAll(expectedResult.stream().filter(pair -> !pair.getFirst().equals(thisYear - 4)).collect(Collectors.toList()));
        assertThat(maintenancePlanRepository.findCorrectiveRepairsByYearForPeriod(thisYear - 4, thisYear - 1))
                .containsAll(expectedResult.stream().filter(pair -> !pair.getFirst().equals(thisYear)).collect(Collectors.toList()));
    }

    @Test
    public void findCorrectiveRepairCostsByYear() {
        int thisYear = LocalDate.now().getYear();
        List<Pair<Integer, BigDecimal>> expectedResult = new ArrayList<>();
        Random random = new Random();

        // We add a random number of corrective tasks per year in a fixed period
        for (int year = thisYear - 4; year <= thisYear; year++) {
            int correctiveTasks = random.nextInt(10) + 1;
            // We assume that the cost per repair is 150
            expectedResult.add(new Pair<>(year, new BigDecimal(correctiveTasks * 150)));
            for (int task = 0; task < correctiveTasks; task++)
                createMaintenanceTaskForYear(year, TypeOfWork.CORRECTIVE, new BigDecimal(150), null);
        }

        assertThat(maintenancePlanRepository
                        .findCorrectiveRepairCostsByYear(thisYear - 4, thisYear)
                        .stream()
                        .map(pair -> new Pair<Integer,BigDecimal>(pair.getFirst(), new BigDecimal(pair.getSecond().longValue())))
                        .collect(Collectors.toList())
                // Please note that we translate BigNumber into Long because comparison of
                // BigNumbers is tricky (we would need to consider range & precision)
        ).containsAll(expectedResult);
    }


    @Test //TODO will delete it later
    public void findCorrectiveRepairCostsByYear2() {
        int thisYear = LocalDate.now().getYear();
        List<Pair<Integer, Long>> expectedResult = new ArrayList<>();
        Random random = new Random();

        // We add a random number of corrective tasks per year in a fixed period
        for (int year = thisYear - 4; year <= thisYear; year++) {
            int correctiveTasks = random.nextInt(10) + 1;
            // We assume that the cost per repair is 150
            expectedResult.add(new Pair<>(year, new Long(correctiveTasks * 150)));
            for (int task = 0; task < correctiveTasks; task++)
                createMaintenanceTaskForYear(year, TypeOfWork.CORRECTIVE, new BigDecimal(150), null);
        }

        assertThat(maintenancePlanRepository
                        .findCorrectiveRepairCostsByYear2(thisYear - 4, thisYear)
                        .stream()
                        .map(pair -> new Pair<Integer,Long>(pair.getFirst(), pair.getSecond().longValue()))
                        .collect(Collectors.toList())
                // Please note that we translate BigNumber into Long because comparison of
                // BigNumbers is tricky (we would need to consider range & precision)
        )
                .containsAll(expectedResult);
    }


}