package com.example.demo.common.domain.validation;

import com.example.demo.common.domain.model.BusinessPeriod;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.temporal.ChronoUnit;
public class BusinessPeriodValidator implements Validator {

    public boolean supports(Class<?> clazz) {
        return BusinessPeriod.class.equals(clazz);
    }

    public void validate(Object object, Errors errors) {
        BusinessPeriod period = (BusinessPeriod) object;
        if (period==null) {
            errors.reject("instance", "BusinessPeriod can not be null");
            return;
        }
        if (period.getStartDate() == null) {
            errors.rejectValue("startDate", "StartDate is missing");
        }
        if (period.getEndDate() == null) {
            errors.rejectValue("endDate", "EndDate is missing");
        }

        if (period.getEndDate().isBefore(period.getStartDate())) {
            errors.reject("dateMismatch", "StartDate should be greater then EndData");

        }

//        if (ChronoUnit.DAYS.between(period.getStartDate(), period.getEndDate()) < 1) {
//            errors.reject("duration", "BusinessPeriod should be more then a data");
//        }
    }
}