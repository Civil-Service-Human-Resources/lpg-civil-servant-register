package uk.gov.cshr.civilservant.service;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.hateoas.Resource;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.Identity;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.domain.Profession;
import uk.gov.cshr.civilservant.exception.UserNotFoundException;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;
import uk.gov.cshr.civilservant.resource.CivilServantResource;
import uk.gov.cshr.civilservant.resource.factory.CivilServantResourceFactory;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReportServiceTest {

    @Mock
    private CivilServantRepository civilServantRepository;

    @Mock
    private CivilServantResourceFactory civilServantResourceFactory;

    @InjectMocks
    private ReportService reportService;

    @Test
    public void shouldReturnListOfCivilServantsByUserOrganisation() {
        String userId = "user-id";

        OrganisationalUnit organisationalUnit = new OrganisationalUnit();

        Identity identity1 = new Identity("1");
        CivilServant civilServant1 = new CivilServant(identity1);
        civilServant1.setOrganisationalUnit(organisationalUnit);

        Identity identity2 = new Identity("2");
        CivilServant civilServant2 = new CivilServant(identity2);

        when(civilServantRepository.findByIdentity(userId)).thenReturn(Optional.of(civilServant1));

        when(civilServantRepository.findAllByOrganisationalUnit(organisationalUnit))
                .thenReturn(Arrays.asList(civilServant1, civilServant2));

        Resource<CivilServantResource> civilServantResource1 = new Resource<>(new CivilServantResource(civilServant1));
        Resource<CivilServantResource> civilServantResource2 = new Resource<>(new CivilServantResource(civilServant2));

        when(civilServantResourceFactory.create(civilServant1)).thenReturn(civilServantResource1);
        when(civilServantResourceFactory.create(civilServant2)).thenReturn(civilServantResource2);

        assertEquals(ImmutableMap.of("1", civilServantResource1, "2", civilServantResource2),
                reportService.getCivilServantMapByUserOrganisation(userId));
    }

    @Test
    public void shouldReturnListOfCivilServantsByUserProfession() {
        String userId = "user-id";

        Profession profession = new Profession("profession");

        Identity identity1 = new Identity("1");
        CivilServant civilServant1 = new CivilServant(identity1);
        civilServant1.setProfession(profession);

        Identity identity2 = new Identity("2");
        CivilServant civilServant2 = new CivilServant(identity2);

        when(civilServantRepository.findByIdentity(userId)).thenReturn(Optional.of(civilServant1));

        when(civilServantRepository.findAllByProfession(profession))
                .thenReturn(Arrays.asList(civilServant1, civilServant2));

        Resource<CivilServantResource> civilServantResource1 = new Resource<>(new CivilServantResource(civilServant1));
        Resource<CivilServantResource> civilServantResource2 = new Resource<>(new CivilServantResource(civilServant2));

        when(civilServantResourceFactory.create(civilServant1)).thenReturn(civilServantResource1);
        when(civilServantResourceFactory.create(civilServant2)).thenReturn(civilServantResource2);

        assertEquals(ImmutableMap.of("1", civilServantResource1, "2", civilServantResource2),
                reportService.getCivilServantMapByUserProfession(userId));
    }

    @Test
    public void shouldThrowUserNotFoundExceptionWhenListingByProfession() {
        String userId = "user-id";

        when(civilServantRepository.findByIdentity(userId)).thenReturn(Optional.empty());

        try {
            reportService.getCivilServantMapByUserProfession(userId);
            fail("Expected UserNotFoundException");
        } catch (UserNotFoundException e) {
            assertEquals("User not found: user-id", e.getMessage());
        }
    }

    @Test
    public void shouldThrowUserNotFoundExceptionWhenListingByOrganisationalUnit() {
        String userId = "user-id";

        when(civilServantRepository.findByIdentity(userId)).thenReturn(Optional.empty());

        try {
            reportService.getCivilServantMapByUserOrganisation(userId);
            fail("Expected UserNotFoundException");
        } catch (UserNotFoundException e) {
            assertEquals("User not found: user-id", e.getMessage());
        }
    }

    @Test
    public void shouldReturnListOfAllCivilServants() {
        Identity identity1 = new Identity("1");
        CivilServant civilServant1 = new CivilServant(identity1);

        Identity identity2 = new Identity("2");
        CivilServant civilServant2 = new CivilServant(identity2);

        when(civilServantRepository.findAll())
                .thenReturn(Arrays.asList(civilServant1, civilServant2));

        Resource<CivilServantResource> civilServantResource1 = new Resource<>(new CivilServantResource(civilServant1));
        Resource<CivilServantResource> civilServantResource2 = new Resource<>(new CivilServantResource(civilServant2));

        when(civilServantResourceFactory.create(civilServant1)).thenReturn(civilServantResource1);
        when(civilServantResourceFactory.create(civilServant2)).thenReturn(civilServantResource2);

        assertEquals(ImmutableMap.of("1", civilServantResource1, "2", civilServantResource2),
                reportService.getCivilServantMap());
    }
}