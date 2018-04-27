package uk.gov.cslearning.civilservant.repository;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cslearning.civilservant.domain.Organisation;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrganisationRepositoryTest {

    @Autowired
    private OrganisationRepository organisationRepository;

    @Test
    public void shouldFindOrganisationByExistingCode() {
        Optional<Organisation> organisation = organisationRepository.findByCode("org");
        assertTrue(organisation.isPresent());
    }

    @Test
    public void shouldNotFindOrganistaionByUnrecognisedCode() {
        Optional<Organisation> organisation = organisationRepository.findByCode("unknown");
        assertFalse(organisation.isPresent());
    }
}
