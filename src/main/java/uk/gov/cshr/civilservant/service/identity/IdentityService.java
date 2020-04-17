package uk.gov.cshr.civilservant.service.identity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.service.exception.UserNotFoundException;

@Service
public class IdentityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdentityService.class);

    private OAuth2RestOperations restOperations;

    private String identityAPIUrl;

    @Autowired
    public IdentityService(OAuth2RestOperations restOperations, @Value("${identity.identityAPIUrl}") String identityAPIUrl) {
        this.restOperations = restOperations;
        this.identityAPIUrl = identityAPIUrl;
    }

    public IdentityFromService findByEmail(String email) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(identityAPIUrl)
                .queryParam("emailAddress", email);

        LOGGER.debug(" Checking email {}", email);
        IdentityFromService identity;

        try {
            identity = restOperations.getForObject(builder.toUriString(), IdentityFromService.class);
        } catch (HttpClientErrorException http) {
            if (http.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null; // we kind of have to assume 403 is email not found rather than service not there ...
            }
            LOGGER.error(" Error with findByEmail when contacting identity service {}", builder.toUriString());
            return null;
        }

        return identity;
    }

    public String getEmailAddress(CivilServant civilServant) {

        LOGGER.debug("Getting email address for civil servant {}", civilServant);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(identityAPIUrl)
                .queryParam("uid", civilServant.getIdentity().getUid());

        IdentityFromService identity;

        try {
            identity = restOperations.getForObject(builder.toUriString(), IdentityFromService.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null;
            }
            throw new UserNotFoundException(e);
        }

        if (identity != null) {
            return identity.getUsername();
        }
        return null;
    }
}
