package com.example.demo.sales.domain.model.validators;

import com.example.demo.sales.domain.model.POStatus;
import com.example.demo.sales.domain.model.PurchaseOrder;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import javax.validation.ConstraintViolation;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;
import java.util.Set;

public class PurchaseOrderValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object object, Errors errors) {
        PurchaseOrder po = (PurchaseOrder) object;
        if (po.getId() == null)
            errors.rejectValue("id", "Purchase Order id cannot be null");
        if (po.getPlantID() == null)
            errors.rejectValue("plantId", "Plant id cannot be null");
//        if (!po.getStatus().equals(POStatus.PENDING)) {
//            if (po.getReservationId() == null)
//                errors.rejectValue("reservationId", "Purchase order's ...");
//
//        errors.pushNestedPath("rentalPeriod");
//        ValidationUtils.invokeValidator(periodValidator, po.getRentalPeriod(), errors);
//        errors.popNestedPath();
    }

//    @Override
//    public <T> Set<ConstraintViolation<T>> validate(T t, Class<?>... classes) {
//        return null;
//    }


}
