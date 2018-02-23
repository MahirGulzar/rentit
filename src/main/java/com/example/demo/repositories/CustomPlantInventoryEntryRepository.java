package com.example.demo.repositories;

import com.example.demo.models.PlantInventoryEntry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomPlantInventoryEntryRepository {

    List<PlantInventoryEntry> findMethod(String name);

//    @Query("select e from PlantInventoryEntry e where e.name like concat('%',:name,'%')")
//    List<PlantInventoryEntry> findMethod(@Param("name") String name);
}
