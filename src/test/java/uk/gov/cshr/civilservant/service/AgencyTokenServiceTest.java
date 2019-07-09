package uk.gov.cshr.civilservant.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.repository.AgencyTokenRepository;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AgencyTokenServiceTest {

    @Mock
    private AgencyTokenRepository agencyTokenRepository;

    @InjectMocks
    private AgencyTokenService agencyTokenService;

    @Test
    public void shouldReturnAgencyTokensAsIterable() {
        Long organisationalUnitId = 1L;

        Iterable<AgencyToken> agencyTokens = new ArrayList<>();

        when(agencyTokenRepository.findAllByOrganisationalUnitEquals(organisationalUnitId)).thenReturn(agencyTokens);

        Iterable<AgencyToken> resultingAgencyTokens = agencyTokenService.getAgencyTokens(organisationalUnitId);

        assertThat(resultingAgencyTokens, equalTo(agencyTokens));
    }
}