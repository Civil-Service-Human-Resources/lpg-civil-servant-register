package uk.gov.cshr.civilservant.validator;

import org.springframework.validation.Errors;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.service.identity.IdentityFromService;

import javax.validation.Validator;

public class IdentityVaiidator  implements Validator {

        @Override
        public boolean supports(Class<?> clazz) {
            return IdentityFromService.class.equals(clazz);
        }

        @Override
        public void validate(Object obj, Errors errors) {
            CivilServant user = (CivilServant) obj;
            if (checkIndentity(user.getLineManager())) {
                errors.rejectValue("lineManager", "404");
            }


        }

        private boolean checkIndentity(CivilServant lineManager) {
            //do something with identity
            return //something
        }
    }


}
