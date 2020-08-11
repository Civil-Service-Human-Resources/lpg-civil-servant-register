package uk.gov.cshr.civilservant.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.Identity;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.domain.Profession;
import uk.gov.cshr.civilservant.dto.CivilServantDto;
import uk.gov.cshr.civilservant.dto.factory.CivilServantDtoFactory;
import uk.gov.cshr.civilservant.exception.UserNotFoundException;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;

@RunWith(MockitoJUnitRunner.class)
public class ReportServiceTest {

  @Mock private CivilServantRepository civilServantRepository;

  @Mock private CivilServantDtoFactory civilServantResourceFactory;

  @InjectMocks private ReportService reportService;

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

    CivilServantDto civilServantDto1 = new CivilServantDto();
    CivilServantDto civilServantDto2 = new CivilServantDto();

    when(civilServantResourceFactory.create(civilServant1)).thenReturn(civilServantDto1);
    when(civilServantResourceFactory.create(civilServant2)).thenReturn(civilServantDto2);

    assertEquals(
        ImmutableMap.of("1", civilServantDto1, "2", civilServantDto2),
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

    CivilServantDto civilServantDto1 = new CivilServantDto();
    CivilServantDto civilServantDto2 = new CivilServantDto();

    when(civilServantResourceFactory.create(civilServant1)).thenReturn(civilServantDto1);
    when(civilServantResourceFactory.create(civilServant2)).thenReturn(civilServantDto2);

    assertEquals(
        ImmutableMap.of("1", civilServantDto1, "2", civilServantDto2),
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

    when(civilServantRepository.findAll()).thenReturn(Arrays.asList(civilServant1, civilServant2));

    CivilServantDto civilServantDto1 = new CivilServantDto();
    CivilServantDto civilServantDto2 = new CivilServantDto();

    when(civilServantResourceFactory.create(civilServant1)).thenReturn(civilServantDto1);
    when(civilServantResourceFactory.create(civilServant2)).thenReturn(civilServantDto2);

    assertEquals(
        ImmutableMap.of("1", civilServantDto1, "2", civilServantDto2),
        reportService.getCivilServantMap());
  }
}
