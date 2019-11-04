package uk.gov.cshr.civilservant.validation;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import uk.gov.cshr.civilservant.dto.AgencyTokenDTO;
import uk.gov.cshr.civilservant.utils.AgencyTokenTestingUtils;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@WithMockUser(username = "user")
public class AgencyTokenDTOValidatorTest {

    @Autowired
    private AgencyTokenDTOValidator classUnderTest;

    @Ignore
    @Test
    public void givenAValidAgencyTokenDTO_whenValidated_thenAgencyTokenDTOValidationPasses() {
        // given
        AgencyTokenDTO validDTO = AgencyTokenTestingUtils.createAgencyTokenDTO();
        Errors errors = new BeanPropertyBindingResult(validDTO, "");

        // when
        classUnderTest.validate(validDTO, errors);

        // then
        assertThat(errors.getAllErrors()).hasSize(0);
    }

    @Test
    public void givenAValidAgencyTokenDTO_whenCapacityUsedIsGreaterThanCapacity_thenAgencyTokenDTOValidationFails() {
        // given
        AgencyTokenDTO capacityUsedGreaterThanCapacityDTO = AgencyTokenTestingUtils.createAgencyTokenDTO();
        capacityUsedGreaterThanCapacityDTO.setCapacityUsed(101);
        Errors errors = new BeanPropertyBindingResult(capacityUsedGreaterThanCapacityDTO, "capacity used must be less than capacity");

        // when
        classUnderTest.validate(capacityUsedGreaterThanCapacityDTO, errors);

        // then
        assertThat(errors.getAllErrors()).hasSize(1);
        assertThat(errors.getFieldError().getField()).isEqualTo("capacityUsed");
        assertThat(errors.getAllErrors().get(0).getDefaultMessage()).isEqualTo("capacity used must be less than capacity");
    }

}
