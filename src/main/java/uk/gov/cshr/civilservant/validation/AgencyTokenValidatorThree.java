package uk.gov.cshr.civilservant.validation;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/*public class AgencyTokenValidatorThree {
}*/
public class AgencyTokenValidatorThree
        implements ConstraintValidator<ValidCapacityUsed, Object> {

    private Integer capacity;
    private Integer capacityUsed;

    public void initialize(ValidCapacityUsed constraintAnnotation) {
        this.capacity = new Integer(constraintAnnotation.capacity());
        this.capacityUsed = new Integer(constraintAnnotation.capacityUsed());
    }

    public boolean isValid(Object value,
                           ConstraintValidatorContext context) {

       /* Object fieldValue = new BeanWrapperImpl(value)
                .getPropertyValue(String.valueOf(capacity));
        Object fieldGreaterThanValue = new BeanWrapperImpl(value)
                .getPropertyValue(String.valueOf(capacityUsed));*/

        if (capacity != null) {
            return capacityUsed.intValue() <= capacity.intValue();
        } else {
            return capacityUsed == null;
        }

    }
}