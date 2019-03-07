package uk.gov.cshr.civilservant.dto.factory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cshr.civilservant.domain.*;
import uk.gov.cshr.civilservant.dto.CivilServantDto;
import uk.gov.cshr.civilservant.service.identity.IdentityService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CivilServantDtoFactoryTest {

    @Mock
    private IdentityService identityService;

    @InjectMocks
    private CivilServantDtoFactory dtoFactory;

    @Test
    public void shouldReturnCivilServantDto() {
        String userId = "uid";
        String organisationName = "test-organisation";
        String professionName = "test-profession";
        String gradeName = "test-grade";
        String userName = "test-user";
        String email = "test@example.com";
        String otherAreaOfWork1 = "other-profession-1";
        String otherAreaOfWork2 = "other-profession-2";

        OrganisationalUnit organisation = new OrganisationalUnit();
        organisation.setName(organisationName);

        CivilServant civilServant = new CivilServant(new Identity(userId));
        civilServant.setProfession(new Profession(professionName));
        civilServant.setOrganisationalUnit(organisation);
        civilServant.setGrade(new Grade("x", gradeName));
        civilServant.setFullName(userName);
        civilServant.setOtherAreasOfWork(new HashSet<>(Arrays.asList(
                new Profession(otherAreaOfWork1), new Profession(otherAreaOfWork2))));

        when(identityService.getEmailAddress(civilServant)).thenReturn(email);

        CivilServantDto dto = dtoFactory.create(civilServant);

        assertEquals(userId, dto.getId());
        assertEquals(organisationName, dto.getOrganisation());
        assertEquals(professionName, dto.getProfession());
        assertEquals(gradeName, dto.getGrade());
        assertEquals(userName, dto.getName());
        assertEquals(email, dto.getEmail());
        assertEquals(Arrays.asList(otherAreaOfWork1, otherAreaOfWork2), dto.getOtherAreasOfWork().stream()
                .sorted().collect(Collectors.toList()));
    }
}