package uk.gov.cshr.civilservant.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import uk.gov.cshr.civilservant.dto.AgencyTokenDTO;

public class AgencyTokenDTOValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return AgencyTokenDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AgencyTokenDTO agencyTokenDTO = (AgencyTokenDTO) target;

        // add spaces available logic
        if(agencyTokenDTO.getCapacityUsed() > agencyTokenDTO.getCapacity()) {
            errors.rejectValue("capacity used", "invalid capacity used");
        }

    }

}