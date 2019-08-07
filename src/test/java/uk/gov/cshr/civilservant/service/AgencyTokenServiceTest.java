package uk.gov.cshr.civilservant.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.repository.AgencyTokenRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AgencyTokenServiceTest {

    @Mock
    private AgencyTokenRepository agencyTokenRepository;

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
}