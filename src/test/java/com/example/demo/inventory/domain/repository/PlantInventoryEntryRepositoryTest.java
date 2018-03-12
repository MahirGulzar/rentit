package com.example.demo.inventory.domain.repository;


import com.example.demo.DemoApplication;
import javafx.application.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoApplication.class)
@Sql(scripts="/plants-dataset.sql")
@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)

public class PlantInventoryEntryRepositoryTest {

    // TODO The Test phase is broken as we are splitting it into separate modules

   /* @Autowired
    PlantInventoryEntryRepository repo;

*//*    @Test
    public void Test1()
    {
//        System.out.println(repo.findAll());
        assertThat(repo.count()).isEqualTo(14);
//        assertThat(repo.findMethod("xca")).hasSize((14));
    }*//*

*/



}
