package uk.gov.cshr.civilservant.validation;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cshr.civilservant.domain.AgencyDomain;
import uk.gov.cshr.civilservant.dto.AgencyDomainDTO;
import uk.gov.cshr.civilservant.dto.AgencyTokenDTO;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@WithMockUser(username = "user")
public class AgencyTokenDTOValidatorTest {

    private Validator validator;

    @Before
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private AgencyTokenDTO createAgencyTokenDTO(){
        AgencyTokenDTO dto = new AgencyTokenDTO();
        dto.setToken("thisisatoken");
        dto.setCapacity(100);
        dto.setCapacityUsed(0);

        Set<AgencyDomainDTO> domains = new HashSet<AgencyDomainDTO>();
        AgencyDomainDTO domainDTO = new AgencyDomainDTO();
        domainDTO.setDomain("aDomain");
        domains.add(domainDTO);

        dto.setAgencyDomains(domains);
        return dto;
    }

    @Test
    public void givenAValidAgencyTokenDTO_whenValidated_thenAgencyTokenDTOValidationPasses() {
        AgencyTokenDTO validDTO = createAgencyTokenDTO();
        Set<ConstraintViolation<AgencyTokenDTO>> violations = validator.validate(validDTO);
        assertThat(violations).isEmpty();
    }

    // TODO - fix this - work in progress
    @Ignore
    @Test
    public void givenAValidAgencyTokenDTO_whenCapacityUsedIsGreaterThanCapacity_thenAgencyTokenDTOValidationFails() {
        AgencyTokenDTO capacityUsedGreaterThanCapacityDTO = createAgencyTokenDTO();
        capacityUsedGreaterThanCapacityDTO.setCapacityUsed(101);
        Set<ConstraintViolation<AgencyTokenDTO>> violations = validator.validate(capacityUsedGreaterThanCapacityDTO);
        assertThat(violations).isNotEmpty();
        Set<ConstraintViolation<AgencyTokenDTO>> s = violations;
        System.out.println("test");
    }
}
