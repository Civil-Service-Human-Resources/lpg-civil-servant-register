package uk.gov.cshr.civilservant.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {MaxCapacityAgencyTokenValidator.class})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxCapacityAgencyToken {

    String message() default "Invalid capacity"; //message to be returned on validation failure

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
