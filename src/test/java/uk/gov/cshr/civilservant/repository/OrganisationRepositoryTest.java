package uk.gov.cshr.civilservant.repository;


import com.google.common.collect.Iterables;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cshr.civilservant.domain.Organisation;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrganisationRepositoryTest {

    @Autowired
    private OrganisationRepository organisationRepository;

    @Test
    public void shouldFindOrganisationsWhereNameStartsWith() {
        Iterable<Organisation> results = organisationRepository.findByNameStartsWithIgnoringCase("Cabi");
        assertThat(Iterables.size(results), is(1));
    }

    @Test
    public void shouldFindOrganisationsWhereNameStartsWithIgnoringCase() {
        Iterable<Organisation> results = organisationRepository.findByNameStartsWithIgnoringCase("cabi");
        assertThat(Iterables.size(results), is(1));
    }

    @Test
    public void shouldNotFindOrganisationsWhereNameDoesNotStartWith() {
        Iterable<Organisation> results = organisationRepository.findByNameStartsWithIgnoringCase("unknown");
        assertThat(Iterables.size(results), is(0));
    }
}
