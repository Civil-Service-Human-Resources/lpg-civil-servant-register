package uk.gov.cshr.civilservant.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target( ElementType.TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = { AgencyTokenValidatorThree.class })
public @interface ValidCapacityUsed {

    String message() default "Invalid capacity used";

    String capacity();

    String capacityUsed();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

