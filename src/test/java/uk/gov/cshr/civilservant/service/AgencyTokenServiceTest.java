package uk.gov.cshr.civilservant.service;

import org.junit.Ignore;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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

    @Ignore
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

}