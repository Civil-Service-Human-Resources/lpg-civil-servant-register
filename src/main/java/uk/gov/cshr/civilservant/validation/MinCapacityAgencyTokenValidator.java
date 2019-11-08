package uk.gov.cshr.civilservant.validation;

import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MinCapacityAgencyTokenValidator implements ConstraintValidator<MinCapacityAgencyToken, Integer> {

    @Value("${agencyToken.capacity.min}")
    protected int minValue;

    @Override
    public void initialize(MinCapacityAgencyToken constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value != null && value >= minValue;
    }

}