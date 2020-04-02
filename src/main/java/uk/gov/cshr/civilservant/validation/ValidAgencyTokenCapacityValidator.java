package uk.gov.cshr.civilservant.validation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class ValidAgencyTokenCapacityValidator implements ConstraintValidator<ValidCapacity, Integer> {

    @Value("${agencyToken.capacity.min}")
    protected int minValue;

    @Value("${agencyToken.capacity.max}")
    protected int maxValue;

    @Override
    public void initialize(ValidCapacity constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value != null && value >= minValue && value <= maxValue;
    }

}