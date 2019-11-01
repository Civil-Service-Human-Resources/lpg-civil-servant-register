package uk.gov.cshr.civilservant.validation;

import uk.gov.cshr.civilservant.domain.AgencyToken;

import javax.validation.*;

public class AgencyTokenValidator {

}
/*public class AgencyTokenValidator
        implements ConstraintValidator<ValidCapacityUsed, AgencyToken> {

    public void initialize(ValidCapacityUsed validCapacityUsed) {

    }

    @Override
    public boolean isValid(AgencyToken agencyToken,
                           ConstraintValidatorContext constraintValidatorContext) {

        // add spaces available logic
        if(agencyToken.getCapacityUsed() > agencyToken.getCapacity()) {
            return false;
            //errors.rejectValue("capacityUsed", "101","capacity used must be less than capacity");
        } else{
            return true;
        }

    }
}*/
