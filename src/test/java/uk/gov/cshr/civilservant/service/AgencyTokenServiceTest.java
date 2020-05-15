package uk.gov.cshr.civilservant.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.repository.AgencyTokenRepository;
import uk.gov.cshr.civilservant.repository.OrganisationalUnitRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AgencyTokenServiceTest {
    @Mock
    private AgencyTokenRepository agencyTokenRepository;

    @Mock
    private OrganisationalUnitRepository organisationalUnitRepository;

    @InjectMocks
    private AgencyTokenService agencyTokenService;

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
    public void givenCodeIsOwnerOfTokenAndDomain_getAgencyTokenByDomainTokenAndOrganisation_returnAgencyToken() {
        String domain = "test.domain";
        AgencyToken agencyToken = new AgencyToken(1, "test-token", 1, "uid");
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
        AgencyToken agencyToken = new AgencyToken(1, "test-token", 1, "uid");
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
        AgencyToken agencyToken = new AgencyToken(1, "test-token", 1, "uid");
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
        AgencyToken agencyToken = new AgencyToken(1, "test-token", 1, "uid");
        OrganisationalUnit organisationalUnit = new OrganisationalUnit("org-name", "org-code", "org-abbrv");

        when(agencyTokenRepository.findByDomainAndToken(domain, agencyToken.getToken())).thenReturn(Optional.empty());

        Optional<AgencyToken> returnedToken = agencyTokenService.getAgencyTokenByDomainTokenAndOrganisation(domain, agencyToken.getToken(), organisationalUnit.getCode());

        assertFalse(returnedToken.isPresent());
    }

    @Test
    public void givenCodeIsNotOwnerOfTokenAndDomain_getAgencyTokenByDomainTokenAndOrganisation_returnEmptyOptional() {
        String domain = "test.domain";
        AgencyToken agencyToken = new AgencyToken(1, "test-token", 1, "uid");
        OrganisationalUnit organisationalUnit = new OrganisationalUnit("org-name", "org-code", "org-abbrv");

        when(agencyTokenRepository.findByDomainAndToken(domain, agencyToken.getToken())).thenReturn(Optional.of(agencyToken));
        when(organisationalUnitRepository.findOrganisationByAgencyToken(agencyToken)).thenReturn(Optional.of(organisationalUnit));

        Optional<AgencyToken> returnedToken = agencyTokenService.getAgencyTokenByDomainTokenAndOrganisation(domain, agencyToken.getToken(), "bad-code");

        assertFalse(returnedToken.isPresent());
    }

    @Test
    public void givenCodeIsNotOwnerNorHasChildOwnerOfTokenAndDomain_getAgencyTokenByDomainTokenAndOrganisation_returnEmptyOptional() {
        String domain = "test.domain";
        AgencyToken agencyToken = new AgencyToken(1, "test-token", 1, "uid");
        OrganisationalUnit parentOrganisationalUnit = new OrganisationalUnit("parent-name", "parent-code", "parent-abbrv");
        OrganisationalUnit childOrganisationalUnit = new OrganisationalUnit("child-name", "child-code", "child-abbrv");
        parentOrganisationalUnit.setChildren(Collections.singletonList(childOrganisationalUnit));

        when(agencyTokenRepository.findByDomainAndToken(domain, agencyToken.getToken())).thenReturn(Optional.of(agencyToken));
        when(organisationalUnitRepository.findOrganisationByAgencyToken(agencyToken)).thenReturn(Optional.of(parentOrganisationalUnit));

        Optional<AgencyToken> returnedToken = agencyTokenService.getAgencyTokenByDomainTokenAndOrganisation(domain, agencyToken.getToken(), "bad-code");

        assertFalse(returnedToken.isPresent());
    }
}
