package uk.gov.cshr.civilservant.service;

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
import uk.gov.cshr.civilservant.repository.CivilServantRepository;
import uk.gov.cshr.civilservant.resource.CivilServantResource;
import uk.gov.cshr.civilservant.resource.factory.CivilServantResourceFactory;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;
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

        assertEquals(Arrays.asList(civilServantResource1, civilServantResource2),
                reportService.listCivilServantsByUserOrganisation(userId));
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

        assertEquals(Arrays.asList(civilServantResource1, civilServantResource2),
                reportService.listCivilServantsByUserProfession(userId));
    }

}