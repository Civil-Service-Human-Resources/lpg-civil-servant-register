package uk.gov.cshr.civilservant.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cshr.civilservant.exception.CSRSApplicationException;
import uk.gov.cshr.civilservant.exception.NoOrganisationsFoundException;
import uk.gov.cshr.civilservant.exception.TokenDoesNotExistException;
import uk.gov.cshr.civilservant.service.identity.IdentityFromService;
import uk.gov.cshr.civilservant.service.identity.IdentityService;

import java.net.MalformedURLException;
import java.net.URI;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class IdentityServiceTest {

    private static final String FIND_BY_EMAIL_URL = "http://localhost/identity";

    private static final String IS_WHITELISTED_URL = "http://localhost:8080/domain/isWhitelisted";

    private String GET_SPACES_USED_URL = "http://localhost:8080/agency/%s";

    private IdentityService identityService;

    private String domain = "domain.com";

    private String EXPECTED_IS_WHITELISTED_URL = "http://localhost:8080/domain/isWhitelisted/domain.com/";

    private String EXPECTED_GET_SPACES_USED_URL = "http://localhost:8080/agency/123";

    @Mock
    private OAuth2RestOperations restOperations;

    @Captor
    private ArgumentCaptor<URI> uriArgumentCaptor;

    @Before
    public void setup() {
        initMocks(this);
        identityService = new IdentityService(restOperations, FIND_BY_EMAIL_URL, IS_WHITELISTED_URL, GET_SPACES_USED_URL);
    }

    @Test
    public void shouldReturnFoundIdentities() {

        IdentityFromService identity = new IdentityFromService();
        identity.setUid("uid");
        identity.setUsername("shouldReturnUriStringFromOrg@domain.com");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(FIND_BY_EMAIL_URL)
                .queryParam("emailAddress", identity.getUsername());

        when(restOperations.getForObject(builder.toUriString(), IdentityFromService.class)).thenReturn(identity);

        IdentityFromService foundIdentity = identityService.findByEmail(identity.getUsername());
        assertThat(foundIdentity.getUsername(), equalTo("shouldReturnUriStringFromOrg@domain.com"));
    }

    @Test
    public void shouldNotReturnNotFoundIdentities() {

        IdentityFromService identity = new IdentityFromService();
        identity.setUid("uid");
        identity.setUsername("shouldReturnUriStringFromOrg@domain.com");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(FIND_BY_EMAIL_URL)
                .queryParam("emailAddress", identity.getUsername());

        when(restOperations.getForObject(builder.toUriString(), IdentityFromService.class)).thenReturn(null);

        IdentityFromService foundIdentity = identityService.findByEmail(identity.getUsername());
        assertNull(foundIdentity);
    }

    @Test
    public void shouldReturnTrueIfWhitelitsed() throws MalformedURLException {
        // given
        ResponseEntity responseEntity = new ResponseEntity<String>("true", HttpStatus.OK);
        when(restOperations.getForEntity(any(URI.class), any())).thenReturn(responseEntity);

        // when
        boolean actual = identityService.isDomainWhiteListed(domain);

        // then
        assertTrue(actual);
        verify(restOperations, times(1)).getForEntity(uriArgumentCaptor.capture(), ArgumentMatchers.any());
        URI actualURI = uriArgumentCaptor.getValue();
        assertThat(actualURI.toURL().toString(), equalTo(EXPECTED_IS_WHITELISTED_URL));
    }

    @Test
    public void shouldReturnFalseIfNotWhitelitsed() throws MalformedURLException {
        // given
        ResponseEntity responseEntity = new ResponseEntity<String>("false", HttpStatus.OK);
        when(restOperations.getForEntity(any(URI.class), any())).thenReturn(responseEntity);

        // when
        boolean actual = identityService.isDomainWhiteListed(domain);

        // then
        assertFalse(actual);
        verify(restOperations, times(1)).getForEntity(uriArgumentCaptor.capture(), ArgumentMatchers.any());
        URI actualURI = uriArgumentCaptor.getValue();
        assertThat(actualURI.toURL().toString(), equalTo(EXPECTED_IS_WHITELISTED_URL));
    }

    @Test (expected = NoOrganisationsFoundException.class)
    public void shouldReturnFalseIfExceptionIsThrown() throws MalformedURLException {
        // given
        when(restOperations.getForEntity(any(URI.class), any())).thenThrow(new RuntimeException());

        // when
        boolean actual = identityService.isDomainWhiteListed(domain);

        // then
        assertFalse(actual);
        verify(restOperations, times(1)).getForEntity(uriArgumentCaptor.capture(), ArgumentMatchers.any());
        URI actualURI = uriArgumentCaptor.getValue();
        assertThat(actualURI.toURL().toString(), equalTo(EXPECTED_IS_WHITELISTED_URL));
    }

    public void shouldReturnNumberOfSpacesIfValid() throws MalformedURLException, CSRSApplicationException {
        // given
        ResponseEntity responseEntity = new ResponseEntity<Integer>(101, HttpStatus.OK);
        when(restOperations.getForObject(any(String.class), any())).thenReturn(responseEntity);

        // when
        int actual = identityService.getSpacesUsedForAgencyToken("123");

        // then
        assertThat(actual, equalTo(101));
        verify(restOperations, times(1)).getForEntity(uriArgumentCaptor.capture(), ArgumentMatchers.any());
        URI actualURI = uriArgumentCaptor.getValue();
        assertThat(actualURI.toURL().toString(), equalTo(EXPECTED_GET_SPACES_USED_URL));
    }

    @Test (expected = TokenDoesNotExistException.class)
    public void shouldThrowNotFoundExceptionIfNoAgencyTokenFound() throws MalformedURLException, CSRSApplicationException {
        // given
        HttpClientErrorException notFoundHttpClientException = new HttpClientErrorException(HttpStatus.NOT_FOUND);
        when(restOperations.getForObject(any(String.class), any())).thenThrow(notFoundHttpClientException);

        // when
        int actual = identityService.getSpacesUsedForAgencyToken("123");

        // then
        assertNull(actual);
        verify(restOperations, times(1)).getForEntity(uriArgumentCaptor.capture(), ArgumentMatchers.any());
        URI actualURI = uriArgumentCaptor.getValue();
        assertThat(actualURI.toURL().toString(), equalTo(EXPECTED_GET_SPACES_USED_URL));
    }

    @Test (expected = CSRSApplicationException.class)
    public void shouldThrowGeneralApplicationExceptionIfUnexpectedError() throws MalformedURLException, CSRSApplicationException {
        // given
        HttpServerErrorException httpServerException = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        when(restOperations.getForObject(any(String.class), any())).thenThrow(httpServerException);

        // when
        int actual = identityService.getSpacesUsedForAgencyToken("123");

        // then
        assertNull(actual);
        verify(restOperations, times(1)).getForEntity(uriArgumentCaptor.capture(), ArgumentMatchers.any());
        URI actualURI = uriArgumentCaptor.getValue();
        assertThat(actualURI.toURL().toString(), equalTo(EXPECTED_GET_SPACES_USED_URL));
    }

}
