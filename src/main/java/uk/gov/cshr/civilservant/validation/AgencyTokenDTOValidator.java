package uk.gov.cshr.civilservant.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import uk.gov.cshr.civilservant.dto.AgencyTokenDTO;

@Component
public class AgencyTokenDTOValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return AgencyTokenDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AgencyTokenDTO agencyTokenDTO = (AgencyTokenDTO) target;

        // ensure when capacity is set, check that the capacity, does not go less than the capacityused
        if(agencyTokenDTO.getCapacity() < agencyTokenDTO.getCapacityUsed()) {
            errors.rejectValue("capacity", "101","capacity cannot be less than capacity used");
        }

    }

}