package com.example.demo.repositories;

import com.example.demo.models.PlantInventoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlantInventoryEntryRepository extends JpaRepository<PlantInventoryEntry,Long> {

    // To Specify by query


    List<PlantInventoryEntry> findByNameLike(String name);
    List<PlantInventoryEntry> findByNameContaining(String name);



//    @Query("select e from PlantInventoryEntry e where e.name like concat('%',:name,'%')")
//    List<PlantInventoryEntry> findMethod(@Param("name") String name);


}
