package uk.gov.cshr.civilservant.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cshr.civilservant.dto.IdentityAgencyResponseDTO;
import uk.gov.cshr.civilservant.exception.CSRSApplicationException;
import uk.gov.cshr.civilservant.exception.NoOrganisationsFoundException;
import uk.gov.cshr.civilservant.exception.TokenDoesNotExistException;
import uk.gov.cshr.civilservant.service.identity.IdentityFromService;
import uk.gov.cshr.civilservant.service.identity.IdentityService;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringRunner.class)
public class IdentityServiceTest {

    private static final String USER_UID = "myuseruid";

    private static final String FIND_BY_EMAIL_URL = "http://localhost/identity";

    private static final String IS_WHITELISTED_URL = "http://localhost:8080/domain/isWhitelisted";

    private String GET_SPACES_USED_URL = "http://localhost:8080/agency/{agencyTokenUid}";

    private String GET_AGENCY_TOKEN_UID_URL = "http://localhost:8080/identity/agency/";

    private IdentityService identityService;

    private String domain = "domain.com";

    private String EXPECTED_IS_WHITELISTED_URL = "http://localhost:8080/domain/isWhitelisted/domain.com/";

    private String EXPECTED_GET_SPACES_USED_URL = "http://localhost:8080/agency/123";

    private String EXPECTED_GET_AGENCY_TOKEN_UID_URL = "http://localhost:8080/identity/agency/" + USER_UID;

    @Mock
    private OAuth2RestOperations restOperations;

    @Captor
    private ArgumentCaptor<URI> uriArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> identityAgencyUrlArgumentCaptor;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
        initMocks(this);
        identityService = new IdentityService(restOperations, FIND_BY_EMAIL_URL, IS_WHITELISTED_URL, GET_SPACES_USED_URL, GET_AGENCY_TOKEN_UID_URL);
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

    @Test
    public void shouldReturnNumberOfSpacesIfValid() throws MalformedURLException, CSRSApplicationException {
        // given
        Integer expectedSpaces = 101;
        String agencyTokenUid = UUID.randomUUID().toString();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(GET_SPACES_USED_URL);

        when(restOperations.getForObject(any(String.class), any())).thenReturn(expectedSpaces);

        // when
        int actual = identityService.getSpacesUsedForAgencyToken(agencyTokenUid);

        // then
        assertThat(actual, equalTo(expectedSpaces));
        verify(restOperations, times(1)).getForObject(builder.buildAndExpand(agencyTokenUid).toUriString(), Integer.class);
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

    @Test
    public void getAgencyTokenUid_ok() throws CSRSApplicationException {
        IdentityAgencyResponseDTO response = new IdentityAgencyResponseDTO();
        response.setAgencyTokenUid("100");
        response.setUid(USER_UID);
        ResponseEntity responseEntity = new ResponseEntity<IdentityAgencyResponseDTO>(response, HttpStatus.OK);
        when(restOperations.getForEntity(any(String.class), any())).thenReturn(responseEntity);

        Optional<String> actual = identityService.getAgencyTokenUid(USER_UID);

        assertThat(actual.get(), equalTo("100"));
        verify(restOperations, times(1)).getForEntity(identityAgencyUrlArgumentCaptor.capture(), ArgumentMatchers.any());
        String actualURL = identityAgencyUrlArgumentCaptor.getValue();
        assertThat(actualURL, equalTo(EXPECTED_GET_AGENCY_TOKEN_UID_URL));
    }

    @Test
    public void getAgencyTokenUid_ok_whenNoAgencyTokenUid_shouldTreatAsOptionalEmpty() throws CSRSApplicationException {
        IdentityAgencyResponseDTO response = new IdentityAgencyResponseDTO();
        response.setUid(USER_UID);
        ResponseEntity responseEntity = new ResponseEntity<IdentityAgencyResponseDTO>(response, HttpStatus.OK);
        when(restOperations.getForEntity(any(String.class), any())).thenReturn(responseEntity);

        Optional<String> actual = identityService.getAgencyTokenUid(USER_UID);

        assertTrue(!actual.isPresent());
        verify(restOperations, times(1)).getForEntity(identityAgencyUrlArgumentCaptor.capture(), ArgumentMatchers.any());
        String actualURL = identityAgencyUrlArgumentCaptor.getValue();
        assertThat(actualURL, equalTo(EXPECTED_GET_AGENCY_TOKEN_UID_URL));
    }

    @Test
    public void getAgencyTokenUid_notFound() throws CSRSApplicationException {
        ResponseEntity responseEntity = new ResponseEntity<IdentityAgencyResponseDTO>(HttpStatus.NOT_FOUND);
        when(restOperations.getForEntity(any(String.class), any())).thenReturn(responseEntity);

        Optional<String> actual = identityService.getAgencyTokenUid(USER_UID);

        assertTrue(!actual.isPresent());
        verify(restOperations, times(1)).getForEntity(identityAgencyUrlArgumentCaptor.capture(), ArgumentMatchers.any());
        String actualURL = identityAgencyUrlArgumentCaptor.getValue();
        assertThat(actualURL, equalTo(EXPECTED_GET_AGENCY_TOKEN_UID_URL));
    }

    @Test
    public void getAgencyTokenUid_technicalError() throws CSRSApplicationException {
        RuntimeException runtimeException = new RuntimeException("broken");
        when(restOperations.getForEntity(any(String.class), any())).thenThrow(runtimeException);
        expectedException.expect(CSRSApplicationException.class);
        expectedException.expectCause(is(RuntimeException.class));
        expectedException.expectMessage("Unexpected error calling identity service: get agency token uid");

        Optional<String> actual = identityService.getAgencyTokenUid(USER_UID);

        assertTrue(!actual.isPresent());
        verify(restOperations, times(1)).getForEntity(identityAgencyUrlArgumentCaptor.capture(), ArgumentMatchers.any());
        String actualURL = identityAgencyUrlArgumentCaptor.getValue();
        assertThat(actualURL, equalTo(EXPECTED_GET_AGENCY_TOKEN_UID_URL));
    }

    @Test
    public void removeAgencyTokenFromUsers_ok() throws CSRSApplicationException {
        String agencyTokenUid = UUID.randomUUID().toString();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(GET_SPACES_USED_URL);
        identityService.removeAgencyTokenFromUsers(agencyTokenUid);
        verify(restOperations, times(1)).delete(builder.buildAndExpand(agencyTokenUid).toUriString());
    }

    @Test(expected = CSRSApplicationException.class)
    public void removeAgencyTokenFromUsers_clientError() throws CSRSApplicationException {
        String agencyTokenUid = UUID.randomUUID().toString();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(GET_SPACES_USED_URL);
        HttpClientErrorException causeException = new HttpClientErrorException(HttpStatus.BAD_REQUEST);

        doThrow(causeException).when(restOperations).delete(builder.buildAndExpand(agencyTokenUid).toUriString());

        try {
            identityService.removeAgencyTokenFromUsers(agencyTokenUid);
        } catch (Exception e) {
            assertEquals("Error calling identity service: delete agency token", e.getMessage());
            assertEquals(causeException, e.getCause());
            verify(restOperations, times(1)).delete(builder.buildAndExpand(agencyTokenUid).toUriString());
            throw e;
        }
    }

    @Test(expected = CSRSApplicationException.class)
    public void removeAgencyTokenFromUsers_serverError() throws CSRSApplicationException {
        String agencyTokenUid = UUID.randomUUID().toString();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(GET_SPACES_USED_URL);
        HttpServerErrorException causeException = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);

        doThrow(causeException).when(restOperations).delete(builder.buildAndExpand(agencyTokenUid).toUriString());

        try {
            identityService.removeAgencyTokenFromUsers(agencyTokenUid);
        } catch (Exception e) {
            assertEquals("Server error calling identity service: delete agency token", e.getMessage());
            assertEquals(causeException, e.getCause());
            verify(restOperations, times(1)).delete(builder.buildAndExpand(agencyTokenUid).toUriString());
            throw e;
        }
    }

    @Test(expected = CSRSApplicationException.class)
    public void removeAgencyTokenFromUsers_unknownError() throws CSRSApplicationException {
        String agencyTokenUid = UUID.randomUUID().toString();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(GET_SPACES_USED_URL);
        Exception causeException = new RuntimeException("Unknown error");

        doThrow(causeException).when(restOperations).delete(builder.buildAndExpand(agencyTokenUid).toUriString());

        try {
            identityService.removeAgencyTokenFromUsers(agencyTokenUid);
        } catch (Exception e) {
            assertEquals("Unexpected error calling identity service: delete agency token", e.getMessage());
            assertEquals(causeException, e.getCause());
            verify(restOperations, times(1)).delete(builder.buildAndExpand(agencyTokenUid).toUriString());
            throw e;
        }
    }

}
