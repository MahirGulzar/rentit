package com.example.demo.inventory.domain.validation;

import com.example.demo.inventory.domain.model.PlantInventoryItem;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PlantInventoryItemValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return PlantInventoryItem.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        PlantInventoryItem plantInventoryItem = (PlantInventoryItem) o;

    }
}
