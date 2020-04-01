package uk.gov.cshr.civilservant.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.exception.NotEnoughSpaceAvailableException;
import uk.gov.cshr.civilservant.exception.TokenDoesNotExistException;
import uk.gov.cshr.civilservant.repository.AgencyTokenRepository;
import uk.gov.cshr.civilservant.repository.OrganisationalUnitRepository;

@RunWith(MockitoJUnitRunner.class)
public class AgencyTokenServiceTest {

    @Captor
    private ArgumentCaptor<AgencyToken> agencyTokenCaptor;

    @Mock
    private AgencyTokenRepository agencyTokenRepository;

    @Mock
    private OrganisationalUnitRepository organisationalUnitRepository;

    @InjectMocks
    private AgencyTokenService agencyTokenService;

    List<String> codes = Arrays.asList("code1", "code2", "123456");

    @Test
    public void getAllAgencyTokens() {
        List<AgencyToken> agencyTokens = new ArrayList<>();

        when(agencyTokenRepository.findAll()).thenReturn(agencyTokens);

        assertEquals(agencyTokens, agencyTokenService.getAllAgencyTokens());
    }

    @Test
    public void getAllAgencyTokensByDomain() {
        List<AgencyToken> agencyTokens = new ArrayList<>();
        String domain = "example.com";

        when(agencyTokenRepository.findAllByDomain(domain)).thenReturn(agencyTokens);

        assertEquals(agencyTokens, agencyTokenService.getAllAgencyTokensByDomain(domain));
    }

    @Test
    public void getAgencyTokenByDomainAndToken() {
        String token = "token123";
        String domain = "example.com";
        AgencyToken agencyToken = new AgencyToken();
        Optional<AgencyToken> optionalAgencyToken = Optional.of(agencyToken);

        when(agencyTokenRepository.findByDomainAndToken(domain, token)).thenReturn(optionalAgencyToken);

        assertEquals(optionalAgencyToken, agencyTokenService.getAgencyTokenByDomainAndToken(domain, token));
    }

    @Test
    public void givenAValidAgencyTokenWithSpaceAvailable_whenIUpdateAgencyTokenSpacesAvailable_thenReturnsSuccessfully() {
        String token = "token123";
        String domain = "example.com";
        String code = "123456";
        List<String> codes = Arrays.asList("code1", "code2", "123456");

        AgencyToken agencyToken = new AgencyToken();
        agencyToken.setToken("thisisatoken");

        int capacity = 100;
        int capacityUsed = 50;
        agencyToken.setCapacity(capacity);
        agencyToken.setCapacityUsed(capacityUsed);
        Optional<AgencyToken> optionalAgencyToken = Optional.of(agencyToken);

        int expectedNewCapacityUsed = capacityUsed + 1;

        // given
        when(agencyTokenRepository.findByDomainTokenAndCodeIncludingAgencyDomains(domain, token, code)).thenReturn(optionalAgencyToken);
        when(agencyTokenRepository.save(any(AgencyToken.class))).thenReturn(new AgencyToken());

        // when
        agencyTokenService.updateAgencyTokenSpacesAvailable(domain, token, codes, false);

        // then
        verify(agencyTokenRepository, times(1)).save(agencyTokenCaptor.capture());

        AgencyToken actualAgencyTokenSavedToDatabase = agencyTokenCaptor.getValue();
        assertThat(actualAgencyTokenSavedToDatabase.getCapacityUsed(), equalTo(expectedNewCapacityUsed));
        assertThat(actualAgencyTokenSavedToDatabase.getCapacity(), equalTo(capacity));
        assertThat(actualAgencyTokenSavedToDatabase.getToken(), equalTo("thisisatoken"));
    }

    @Test
    public void givenAValidAgencyAndIsARemoveUser_whenIUpdateAgencyTokenSpacesAvailable_thenReturnsSuccessfully() {
        String token = "token123";
        String domain = "example.com";
        String code = "123456";
        List<String> codes = Arrays.asList("code1", "123456", "code3");

        AgencyToken agencyToken = new AgencyToken();
        agencyToken.setToken("thisisatoken");

        int capacity = 100;
        int capacityUsed = 50;
        agencyToken.setCapacity(capacity);
        agencyToken.setCapacityUsed(capacityUsed);
        Optional<AgencyToken> optionalAgencyToken = Optional.of(agencyToken);

        // should free up capacity by 1
        int expectedNewCapacityUsed = capacityUsed - 1;

        // given
        when(agencyTokenRepository.findByDomainTokenAndCodeIncludingAgencyDomains(domain, token, code)).thenReturn(optionalAgencyToken);
        when(agencyTokenRepository.save(any(AgencyToken.class))).thenReturn(new AgencyToken());

        // when
        agencyTokenService.updateAgencyTokenSpacesAvailable(domain, token, codes, true);

        // then
        verify(agencyTokenRepository, times(1)).save(agencyTokenCaptor.capture());

        AgencyToken actualAgencyTokenSavedToDatabase = agencyTokenCaptor.getValue();
        assertThat(actualAgencyTokenSavedToDatabase.getCapacityUsed(), equalTo(expectedNewCapacityUsed));
        assertThat(actualAgencyTokenSavedToDatabase.getCapacity(), equalTo(capacity));
        assertThat(actualAgencyTokenSavedToDatabase.getToken(), equalTo("thisisatoken"));
    }

    @Test
    public void givenANotInExistanceAgencyToken_whenIUpdateAgencyTokenSpacesAvailable_thenTokenDoesNotExistExceptionShouldBeThrown() {
        String token = "token123";
        String domain = "example.com";
        String code = "123456";
        List<String> codes = Arrays.asList("code1", "code2", "123456");

        // given
        when(agencyTokenRepository.findByDomainTokenAndCodeIncludingAgencyDomains(domain, token, code)).thenThrow(new TokenDoesNotExistException(domain));

        // when
        assertThatThrownBy(() -> agencyTokenService.updateAgencyTokenSpacesAvailable(domain, token, codes, false))
                .isInstanceOf(TokenDoesNotExistException.class);

        // then
        verify(agencyTokenRepository, never()).save(any(AgencyToken.class));
    }

    @Test
    public void givenAValidAgencyTokenWithNoSpacesAvailable_whenIUpdateAgencyTokenSpacesAvailable_thenNotEnoughSpaceAvailableExceptionShouldBeThrown() {
        String token = "token123";
        String domain = "example.com";
        String code = "123456";
        List<String> codes = Arrays.asList("code1", "123456", "code3");

        AgencyToken agencyToken = new AgencyToken();
        agencyToken.setToken("thisisatoken");

        int capacity = 100;
        int capacityUsed = 100;
        agencyToken.setCapacity(capacity);
        agencyToken.setCapacityUsed(capacityUsed);
        Optional<AgencyToken> optionalAgencyToken = Optional.of(agencyToken);

        // given
        when(agencyTokenRepository.findByDomainTokenAndCodeIncludingAgencyDomains(domain, token, code)).thenReturn(optionalAgencyToken);

        // when
        assertThatThrownBy(() -> agencyTokenService.updateAgencyTokenSpacesAvailable(domain, token, codes, false))
                .isInstanceOf(NotEnoughSpaceAvailableException.class);

        // then
        verify(agencyTokenRepository, never()).save(any(AgencyToken.class));
    }

    @Test
    public void givenADatabaseError_whenIUpdateAgencyTokenSpacesAvailable_thenExceptionShouldBeThrown() {
        String token = "token123";
        String domain = "example.com";
        String code = "123456";
        List<String> codes = Arrays.asList("code1", "code2", "123456");


        // given
        when(agencyTokenRepository.findByDomainTokenAndCodeIncludingAgencyDomains(domain, token, code)).thenThrow(new RuntimeException());

        // when
        assertThatThrownBy(() -> agencyTokenService.updateAgencyTokenSpacesAvailable(domain, token, codes, false))
                .isInstanceOf(Exception.class);

        // then
        verify(agencyTokenRepository, never()).save(any(AgencyToken.class));
    }

    @Test
    public void givenAValidAgencyTokenWithOnlyOneSpaceAvailableAndIsRemoveAUser_whenIUpdateAgencyTokenSpacesAvailable_thenReturnsSuccessfully() {
        String token = "token123";
        String domain = "example.com";
        String code = "123456";
        List<String> codes = Arrays.asList("123456", "code2", "code3");


        AgencyToken agencyToken = new AgencyToken();
        agencyToken.setToken("thisisatoken");

        int capacity = 100;
        int capacityUsed = 1;
        agencyToken.setCapacity(capacity);
        agencyToken.setCapacityUsed(capacityUsed);
        Optional<AgencyToken> optionalAgencyToken = Optional.of(agencyToken);

        // should free up capacity by 1
        int expectedNewCapacityUsed = 0;

        // given
        when(agencyTokenRepository.findByDomainTokenAndCodeIncludingAgencyDomains(domain, token, code)).thenReturn(optionalAgencyToken);
        when(agencyTokenRepository.save(any(AgencyToken.class))).thenReturn(new AgencyToken());

        // when
        agencyTokenService.updateAgencyTokenSpacesAvailable(domain, token, codes, true);

        // then
        verify(agencyTokenRepository, times(1)).save(agencyTokenCaptor.capture());

        AgencyToken actualAgencyTokenSavedToDatabase = agencyTokenCaptor.getValue();
        assertThat(actualAgencyTokenSavedToDatabase.getCapacityUsed(), equalTo(expectedNewCapacityUsed));
        assertThat(actualAgencyTokenSavedToDatabase.getCapacity(), equalTo(capacity));
        assertThat(actualAgencyTokenSavedToDatabase.getToken(), equalTo("thisisatoken"));
    }

    @Test
    public void givenAValidAgencyTokenWithZeroSpaceAvailableAndIsRemoveAUser_whenIUpdateAgencyTokenSpacesAvailable_thenExceptionShouldBeThrown() {
        String token = "token123";
        String domain = "example.com";
        String code = "123456";
        List<String> codes = Arrays.asList("123456", "code2", "code3");

        AgencyToken agencyToken = new AgencyToken();
        agencyToken.setToken("thisisatoken");

        int capacity = 100;
        int capacityUsed = 0;
        agencyToken.setCapacity(capacity);
        agencyToken.setCapacityUsed(capacityUsed);
        Optional<AgencyToken> optionalAgencyToken = Optional.of(agencyToken);

        // given
        when(agencyTokenRepository.findByDomainTokenAndCodeIncludingAgencyDomains(domain, token, code)).thenReturn(optionalAgencyToken);

        // when
        assertThatThrownBy(() -> agencyTokenService.updateAgencyTokenSpacesAvailable(domain, token, codes, true))
                .isInstanceOf(NotEnoughSpaceAvailableException.class);

        // then
        verify(agencyTokenRepository, never()).save(any(AgencyToken.class));
    }

    @Test
    public void givenCodeIsOwnerOfTokenAndDomain_getAgencyTokenByDomainTokenAndOrganisation_returnAgencyToken() {
        String domain = "test.domain";
        AgencyToken agencyToken = new AgencyToken(1, "test-token", 1, 0);
        OrganisationalUnit organisationalUnit = new OrganisationalUnit("org-name", "org-code", "org-abbrv");

        when(agencyTokenRepository.findByDomainAndToken(domain, agencyToken.getToken())).thenReturn(Optional.of(agencyToken));
        when(organisationalUnitRepository.findOrganisationByAgencyToken(agencyToken)).thenReturn(Optional.of(organisationalUnit));

        Optional<AgencyToken> returnedToken = agencyTokenService.getAgencyTokenByDomainTokenAndOrganisation(domain, agencyToken.getToken(), organisationalUnit.getCode());

        assertTrue(returnedToken.isPresent());
        assertEquals(agencyToken, returnedToken.get());
    }

    @Test
    public void givenCodeHasChildOwnerOfTokenAndDomain_getAgencyTokenByDomainTokenAndOrganisation_returnAgencyToken() {
        String domain = "test.domain";
        AgencyToken agencyToken = new AgencyToken(1, "test-token", 1, 0);
        OrganisationalUnit parentOrganisationalUnit = new OrganisationalUnit("parent-name", "parent-code", "parent-abbrv");
        OrganisationalUnit childOrganisationalUnit = new OrganisationalUnit("child-name", "child-code", "child-abbrv");
        parentOrganisationalUnit.setChildren(Collections.singletonList(childOrganisationalUnit));

        when(agencyTokenRepository.findByDomainAndToken(domain, agencyToken.getToken())).thenReturn(Optional.of(agencyToken));
        when(organisationalUnitRepository.findOrganisationByAgencyToken(agencyToken)).thenReturn(Optional.of(parentOrganisationalUnit));

        Optional<AgencyToken> returnedToken = agencyTokenService.getAgencyTokenByDomainTokenAndOrganisation(domain, agencyToken.getToken(), childOrganisationalUnit.getCode());

        assertTrue(returnedToken.isPresent());
        assertEquals(agencyToken, returnedToken.get());
    }

    @Test
    public void givenCodeHasGrandchildOwnerOfTokenAndDomain_getAgencyTokenByDomainTokenAndOrganisation_returnAgencyToken() {
        String domain = "test.domain";
        AgencyToken agencyToken = new AgencyToken(1, "test-token", 1, 0);
        OrganisationalUnit parentOrganisationalUnit = new OrganisationalUnit("parent-name", "parent-code", "parent-abbrv");
        OrganisationalUnit childOrganisationalUnit = new OrganisationalUnit("child-name", "child-code", "child-abbrv");
        OrganisationalUnit grandchildOrganisationalUnit = new OrganisationalUnit("grandchild-name", "grandchild-code", "grandchild-abbrv");

        parentOrganisationalUnit.setChildren(Collections.singletonList(childOrganisationalUnit));
        childOrganisationalUnit.setChildren(Collections.singletonList(grandchildOrganisationalUnit));

        when(agencyTokenRepository.findByDomainAndToken(domain, agencyToken.getToken())).thenReturn(Optional.of(agencyToken));
        when(organisationalUnitRepository.findOrganisationByAgencyToken(agencyToken)).thenReturn(Optional.of(parentOrganisationalUnit));

        Optional<AgencyToken> returnedToken = agencyTokenService.getAgencyTokenByDomainTokenAndOrganisation(domain, agencyToken.getToken(), grandchildOrganisationalUnit.getCode());

        assertTrue(returnedToken.isPresent());
        assertEquals(agencyToken, returnedToken.get());
    }

    @Test
    public void givenTokenDoesntExist_getAgencyTokenByDomainTokenAndOrganisation_returnEmptyOptional() {
        String domain = "test.domain";
        AgencyToken agencyToken = new AgencyToken(1, "test-token", 1, 0);
        OrganisationalUnit organisationalUnit = new OrganisationalUnit("org-name", "org-code", "org-abbrv");

        when(agencyTokenRepository.findByDomainAndToken(domain, agencyToken.getToken())).thenReturn(Optional.empty());

        Optional<AgencyToken> returnedToken = agencyTokenService.getAgencyTokenByDomainTokenAndOrganisation(domain, agencyToken.getToken(), organisationalUnit.getCode());

        assertFalse(returnedToken.isPresent());
    }

    @Test
    public void givenCodeIsNotOwnerOfTokenAndDomain_getAgencyTokenByDomainTokenAndOrganisation_returnEmptyOptional() {
        String domain = "test.domain";
        AgencyToken agencyToken = new AgencyToken(1, "test-token", 1, 0);
        OrganisationalUnit organisationalUnit = new OrganisationalUnit("org-name", "org-code", "org-abbrv");

        when(agencyTokenRepository.findByDomainAndToken(domain, agencyToken.getToken())).thenReturn(Optional.of(agencyToken));
        when(organisationalUnitRepository.findOrganisationByAgencyToken(agencyToken)).thenReturn(Optional.of(organisationalUnit));

        Optional<AgencyToken> returnedToken = agencyTokenService.getAgencyTokenByDomainTokenAndOrganisation(domain, agencyToken.getToken(), "bad-code");

        assertFalse(returnedToken.isPresent());
    }

    @Test
    public void givenCodeIsNotOwnerNorHasChildOwnerOfTokenAndDomain_getAgencyTokenByDomainTokenAndOrganisation_returnEmptyOptional() {
        String domain = "test.domain";
        AgencyToken agencyToken = new AgencyToken(1, "test-token", 1, 0);
        OrganisationalUnit parentOrganisationalUnit = new OrganisationalUnit("parent-name", "parent-code", "parent-abbrv");
        OrganisationalUnit childOrganisationalUnit = new OrganisationalUnit("child-name", "child-code", "child-abbrv");
        parentOrganisationalUnit.setChildren(Collections.singletonList(childOrganisationalUnit));

        when(agencyTokenRepository.findByDomainAndToken(domain, agencyToken.getToken())).thenReturn(Optional.of(agencyToken));
        when(organisationalUnitRepository.findOrganisationByAgencyToken(agencyToken)).thenReturn(Optional.of(parentOrganisationalUnit));

        Optional<AgencyToken> returnedToken = agencyTokenService.getAgencyTokenByDomainTokenAndOrganisation(domain, agencyToken.getToken(), "bad-code");

        assertFalse(returnedToken.isPresent());
    }
}
