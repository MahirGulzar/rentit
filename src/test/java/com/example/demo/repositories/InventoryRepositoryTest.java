package com.example.demo.repositories;

import com.example.demo.DemoApplication;
import com.example.demo.models.PlantInventoryEntry;
import javafx.application.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.assertj.core.api.Assertions.assertThat;

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = DemoApplication.class)
//@Sql(scripts="/plants-dataset.sql")
//@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class InventoryRepositoryTest {
//    @Autowired
//    PlantInventoryEntryRepository plantInventoryEntryRepo;
//    @Autowired
//    PlantInventoryItemRepository plantInventorItemRepo;
//    @Autowired
//    PlantReservationRepository plantReservationRepo;
//    @Autowired
//    InventoryRepository inventoryRepo;

//    @Test
    public void queryPlantCatalog() {
        //assertThat(plantInventoryEntryRepo.count()).isEqualTo(14l);
    }

//    @Test
    public void queryByName() {
        //assertThat(plantInventoryEntryRepo.findByNameContaining("Mini").size()).isEqualTo(2);
    }

//    @Test
    public void findAvailableTest() {
        //PlantInventoryEntry entry = plantInventoryEntryRepo.findOne(1l);
//        PlantInventoryItem item = plantInventoryItemRepo.findOneByPlantInfo(entry);
//
//        assertThat(invetoryRepo.findAvailablePlants(LocalDate.of(2017,2,20), LocalDate.of(2017,2,25)))
//                .contains(entry);
//
//        PlantReservation po = new PlantReservation();
//        po.setPlant(item);
//        po.setSchedule(BusinessPeriod.of(LocalDate.of(2017, 2, 20), LocalDate.of(2017, 2, 25)));
//        plantReservationRepo.save(po);
//
//        assertThat(inventoryRepo.findAvailablePlants(LocalDate.of(2017,2,20), LocalDate.of(2017,2,25)))
//                .doesNotContain(entry);
    }
}