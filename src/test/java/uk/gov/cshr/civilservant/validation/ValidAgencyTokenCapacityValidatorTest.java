package uk.gov.cshr.civilservant.validation;

import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@WithMockUser(username = "user")
public class ValidAgencyTokenCapacityValidatorTest {

    @Autowired
    private ValidAgencyTokenCapacityValidator classUnderTest;

    @Mock
    private ConstraintValidatorContextImpl constraintValidatorContext;

    @Value("${agencyToken.capacity.min}")
    private int minValueFromConfig;

    @Value("${agencyToken.capacity.max}")
    private int maxValueFromConfig;

    @Test
    public void givenAValidCapacity_whenValidated_thenAgencyTokenDTOValidationPasses() {
        // given
        Integer capacity = minValueFromConfig + 1;

        // when
        boolean actual = classUnderTest.isValid(capacity, constraintValidatorContext);

        // then
        assertTrue(actual);
    }

    @Test
    public void givenAnInValidCapacityGreaterThanMax_whenValidated_thenAgencyTokenDTOValidationFails() {
        // given
        Integer capacity = maxValueFromConfig + 1;

        // when
        boolean actual = classUnderTest.isValid(capacity, constraintValidatorContext);

        // then
        assertFalse(actual);
    }

    @Test
    public void givenAnInValidCapacityLessThanMin_whenValidated_thenAgencyTokenDTOValidationFails() {
        // given
        Integer capacity = minValueFromConfig - 1;

        // when
        boolean actual = classUnderTest.isValid(capacity, constraintValidatorContext);

        // then
        assertFalse(actual);
    }

}
