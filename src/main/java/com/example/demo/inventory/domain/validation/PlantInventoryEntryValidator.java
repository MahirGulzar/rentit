package com.example.demo.inventory.domain.validation;

import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;

public class PlantInventoryEntryValidator implements Validator {


    @Override
    public boolean supports(Class<?> aClass) {
        return PlantInventoryEntry.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        PlantInventoryEntry plantInventoryEntry = (PlantInventoryEntry) o;
        if(plantInventoryEntry == null) {
            errors.reject("instance", "Plant cannot be null");
            return;
        }
        if(plantInventoryEntry.getId() == null) {
            errors.rejectValue("id", "PlantInventoryEntry id cannot be null");
        }
        if (plantInventoryEntry.getDescription() == null) {
            errors.rejectValue("description", "Description cannot be null");
        }
        if(plantInventoryEntry.getName() == null) {
            errors.rejectValue("name", "Name cannot be null");
        }
        if(plantInventoryEntry.getPrice().getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            errors.rejectValue("price", "Price cannot be negative or zero");
        }

    }
}
