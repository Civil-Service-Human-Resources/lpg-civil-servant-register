package uk.gov.cshr.civilservant.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.exception.NotEnoughSpaceAvailableException;
import uk.gov.cshr.civilservant.exception.TokenDoesNotExistException;
import uk.gov.cshr.civilservant.repository.AgencyTokenRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AgencyTokenServiceTest {

    @Captor
    private ArgumentCaptor<AgencyToken> agencyTokenCaptor;

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

    @Test
    public void givenAValidAgencyTokenWithSpaceAvailable_whenIUpdateAgencyTokenSpacesAvailable_thenReturnsSuccessfully() {
        String token = "token123";
        String domain = "example.com";
        String code = "123456";

        AgencyToken agencyToken = new AgencyToken();
        agencyToken.setToken("thisisatoken");

        int capacity = 100;
        int capacityUsed = 50;
        agencyToken.setCapacity(capacity);
        agencyToken.setCapacityUsed(capacityUsed);
        Optional<AgencyToken> optionalAgencyToken = Optional.of(agencyToken);

        int expectedNewCapacityUsed = capacityUsed + 1;

        // given
        when(agencyTokenRepository.findByDomainTokenAndCode(domain, token, code)).thenReturn(optionalAgencyToken);
        when(agencyTokenRepository.save(any(AgencyToken.class))).thenReturn(new AgencyToken());

        // when
        Optional<AgencyToken> actual = agencyTokenService.updateAgencyTokenSpacesAvailable(domain, token, code, false);

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

        // given
        when(agencyTokenRepository.findByDomainTokenAndCode(domain, token, code)).thenThrow(new TokenDoesNotExistException(domain));

        // when
        // then
        assertThatThrownBy(() -> agencyTokenService.updateAgencyTokenSpacesAvailable(domain, token, code, false))
                .isInstanceOf(TokenDoesNotExistException.class);

        verify(agencyTokenRepository, never()).save(agencyTokenCaptor.capture());
    }

    @Test
    public void givenAValidAgencyTokenWithNoSpacesAvailable_whenIUpdateAgencyTokenSpacesAvailable_thenNotEnoughSpaceAvailableExceptionShouldBeThrown() {
        String token = "token123";
        String domain = "example.com";
        String code = "123456";

        AgencyToken agencyToken = new AgencyToken();
        agencyToken.setToken("thisisatoken");

        int capacity = 100;
        int capacityUsed = 100;
        agencyToken.setCapacity(capacity);
        agencyToken.setCapacityUsed(capacityUsed);
        Optional<AgencyToken> optionalAgencyToken = Optional.of(agencyToken);

        // given
        when(agencyTokenRepository.findByDomainTokenAndCode(domain, token, code)).thenReturn(optionalAgencyToken);

        // when
        // then
        assertThatThrownBy(() -> agencyTokenService.updateAgencyTokenSpacesAvailable(domain, token, code, false))
                .isInstanceOf(NotEnoughSpaceAvailableException.class);

        verify(agencyTokenRepository, never()).save(agencyTokenCaptor.capture());
    }

    @Test
    public void givenADatabaseError_whenIUpdateAgencyTokenSpacesAvailable_thenExceptionShouldBeThrown() {
        String token = "token123";
        String domain = "example.com";
        String code = "123456";

        AgencyToken agencyToken = new AgencyToken();
        agencyToken.setToken("thisisatoken");

        int capacity = 100;
        int capacityUsed = 100;
        agencyToken.setCapacity(capacity);
        agencyToken.setCapacityUsed(capacityUsed);
        Optional<AgencyToken> optionalAgencyToken = Optional.of(agencyToken);

        // given
        when(agencyTokenRepository.findByDomainTokenAndCode(domain, token, code)).thenThrow(new RuntimeException());

        // when
        // then
        assertThatThrownBy(() -> agencyTokenService.updateAgencyTokenSpacesAvailable(domain, token, code, false))
                .isInstanceOf(Exception.class);

        verify(agencyTokenRepository, never()).save(agencyTokenCaptor.capture());
    }

}