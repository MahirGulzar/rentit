package com.example.demo.models;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.persistence.Embeddable;
import java.time.LocalDate;

@Embeddable
@Value      // this annotation enforces immutability that is all attributes are hidden and only getters are generated
@NoArgsConstructor(force = true,access= AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "of")
public class BusinessPeriod {
    LocalDate startDate;
    LocalDate endDate;
}
