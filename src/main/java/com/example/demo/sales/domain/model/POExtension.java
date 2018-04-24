package com.example.demo.sales.domain.model;

import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;

@Embeddable
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class POExtension {
    public  enum Status{PENDING, ACCEPTED, REJECTED}

    LocalDate endDate;
    @Enumerated(EnumType.STRING)
    Status status;
}

