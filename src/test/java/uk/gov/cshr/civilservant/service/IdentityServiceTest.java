package uk.gov.cshr.civilservant.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cshr.civilservant.service.identity.IdentityFromService;
import uk.gov.cshr.civilservant.service.identity.IdentityService;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class IdentityServiceTest {

    private static final String FIND_BY_EMAIL_URL = "http://localhost/identity";

    private static final String IS_WHITELISTED_URL = "http://localhost/domain/isWhitelisted";

    private IdentityService identityService;

    @Mock
    private OAuth2RestOperations restOperations;

    @Before
    public void setup() {
        initMocks(this);
        identityService = new IdentityService(restOperations, FIND_BY_EMAIL_URL, IS_WHITELISTED_URL);
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
    public void shouldReturnTrueIfWhitelitsed() {
        // given
        String domain = "myDomain";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(IS_WHITELISTED_URL)
                .queryParam("domain", domain);
        when(restOperations.getForObject(builder.toUriString(), Boolean.class)).thenReturn(true);

        // when
        boolean actual = identityService.isDomainWhiteListed(domain);

        // then
        assertTrue(actual);
    }

    @Test
    public void shouldReturnFalseIfNotWhitelitsed() {
        // given
        String domain = "myDomain";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(IS_WHITELISTED_URL)
                .queryParam("domain", domain);
        when(restOperations.getForObject(builder.toUriString(), Boolean.class)).thenReturn(false);

        // when
        boolean actual = identityService.isDomainWhiteListed(domain);

        // then
        assertFalse(actual);
    }

    @Test
    public void shouldReturnFalseIfExceptionIsThrown() {
        // given
        String domain = "myDomain";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(IS_WHITELISTED_URL)
                .queryParam("domain", domain);
        when(restOperations.getForObject(builder.toUriString(), Boolean.class)).thenThrow(new RuntimeException());

        // when
        boolean actual = identityService.isDomainWhiteListed(domain);

        // then
        assertFalse(actual);
    }
}