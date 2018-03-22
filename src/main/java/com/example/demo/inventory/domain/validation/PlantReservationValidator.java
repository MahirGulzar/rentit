package com.example.demo.inventory.domain.validation;

import com.example.demo.inventory.domain.model.PlantReservation;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PlantReservationValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return PlantReservation.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        PlantReservation plantReservation = (PlantReservation) o;
    }
}
