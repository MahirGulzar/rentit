package com.example.demo.sales.domain.validation;

import com.example.demo.common.domain.model.BusinessPeriod;
import com.example.demo.common.domain.validation.BusinessPeriodIsInFutureValidator;
import com.example.demo.common.domain.validation.BusinessPeriodValidator;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.sales.domain.repository.PurchaseOrderRepository;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.validation.PlantInventoryEntryValidator;
import com.example.demo.sales.domain.model.PurchaseOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.math.BigDecimal;


public class PurchaseOrderValidator implements Validator {

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    PlantInventoryEntryRepository inventoryRepository;

    private final BusinessPeriodValidator periodValidator;
    private final PlantInventoryEntryValidator plantValidator;
    private final BusinessPeriodIsInFutureValidator futureValidator;

    public PurchaseOrderValidator(PlantInventoryEntryValidator plantValidator, BusinessPeriodValidator periodValidator, BusinessPeriodIsInFutureValidator futureValidator) {

        if(plantValidator == null) {
            throw new IllegalArgumentException("The [PlantInventoryEntryValidator] is required and must not be null.");
        }

        if (!plantValidator.supports(PlantInventoryEntry.class)) {
            throw new IllegalArgumentException("The [PlantInventoryEntryValidator] must support the validation of [PlantInventoryEntry] instances.");
        }

        if (periodValidator == null) {
            throw new IllegalArgumentException("The [BusinessPeriodValidator] is required and must not be null.");
        }
        if (!periodValidator.supports(BusinessPeriod.class)) {
            throw new IllegalArgumentException("The [BusinessPeriodValidator] must support the validation of [BusinessPeriod] instances.");
        }

        if (futureValidator == null) {
            throw new IllegalArgumentException("The [BusinessPeriodIsInFutureValidator] is required and must not be null.");
        }
        if (!futureValidator.supports(BusinessPeriod.class)) {
            throw new IllegalArgumentException("The supplied [BusinessPeriodIsInFutureValidator] must " +
                    "support the validation of [BusinessPeriod] instances.");
        }
        this.periodValidator = periodValidator;
        this.plantValidator = plantValidator;
        this.futureValidator = futureValidator;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return PurchaseOrder.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        PurchaseOrder order = (PurchaseOrder)o;
        if(order == null) {
            errors.reject("instance", "PurchaseOrder can't be null");
            return;
        }
        if (order.getId() == null) {
            errors.rejectValue("id", "PurchaseOrder id can't be null");
        }

        if (order.getTotal() == null || order.getTotal().compareTo(BigDecimal.ZERO) < 0) {
            errors.rejectValue("total", "Payment can't be null should be and positive");
        }

        try {
            errors.pushNestedPath("plant");
            ValidationUtils.invokeValidator(plantValidator, order.getPlant(), errors);
        }
        finally {
            errors.popNestedPath();
        }

        try {
            errors.pushNestedPath("rentalPeriod");
            ValidationUtils.invokeValidator(periodValidator, order.getRentalPeriod(), errors);
        }
        finally {
            errors.popNestedPath();
        }
        try {
            errors.pushNestedPath("futurePeriod");
            ValidationUtils.invokeValidator(futureValidator, order.getRentalPeriod(), errors);
        }
        finally {
            errors.popNestedPath();
        }
    }
}

