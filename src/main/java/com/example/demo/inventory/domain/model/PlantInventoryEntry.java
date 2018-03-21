package com.example.demo.inventory.domain.model;

import com.example.demo.common.domain.model.Money;
import lombok.*;

import javax.persistence.*;

@Entity
//@Data
@Getter
@NoArgsConstructor(force = true,access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class PlantInventoryEntry {

    @Id @GeneratedValue
    Long id;

    String name;
    String description;

    @Embedded
    Money price;

}
