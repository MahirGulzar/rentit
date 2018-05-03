package com.example.demo.inventory.domain.model;

import com.example.demo.common.domain.model.Money;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
@Entity
@Getter
@NoArgsConstructor(force = true,access = AccessLevel.PROTECTED)
//@AllArgsConstructor(staticName = "of")
@Data
public class PlantInventoryEntry {
    @Id @GeneratedValue
    Long id;

    String name;
    String description;

    @Column(precision = 8, scale = 2)
    BigDecimal price;

    public static PlantInventoryEntry of(Long id, String name, String description, BigDecimal price) {
        PlantInventoryEntry plant = new PlantInventoryEntry();
        plant.id = id ;
        plant.name = name;
        plant.description = description;
        plant.price = price;
        return plant;
    }
}
