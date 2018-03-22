package com.example.demo.common.domain.validation;

import com.example.demo.common.domain.model.BusinessPeriod;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

public class BusinessPeriodIsInFutureValidator implements Validator {

    public boolean supports(Class<?> clazz) {
        return BusinessPeriod.class.equals(clazz);
    }

    public void validate(Object object, Errors errors) {
        BusinessPeriod period = (BusinessPeriod) object;

        LocalDate now = LocalDate.now();
        if(period.getStartDate().isBefore(now)) {
            errors.reject("timing", "BusinessPeriod should be in the future");
        }
    }
}
