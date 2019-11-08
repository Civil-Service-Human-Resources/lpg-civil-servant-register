package uk.gov.cshr.civilservant.validation;

import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MaxCapacityAgencyTokenValidator implements ConstraintValidator<MaxCapacityAgencyToken, Integer> {

    @Value("${agencyToken.capacity.max}")
    protected int maxValue;

    @Override
    public void initialize(MaxCapacityAgencyToken constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value != null && value <= maxValue;
    }

}