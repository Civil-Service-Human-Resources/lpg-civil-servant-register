package uk.gov.cshr.civilservant.service.identity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.exception.CSRSApplicationException;
import uk.gov.cshr.civilservant.exception.NoOrganisationsFoundException;
import uk.gov.cshr.civilservant.exception.TokenDoesNotExistException;
import uk.gov.cshr.civilservant.service.exception.UserNotFoundException;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@Service
public class IdentityService {

    private OAuth2RestOperations restOperations;

    private String identityAPIUrl;

    private String identityWhiteListUrl;

    private String identityAgencyTokenUrl;

    @Autowired
    public IdentityService(OAuth2RestOperations restOperations, @Value("${identity.identityAPIUrl}") String identityAPIUrl,
                           @Value("${identity.identityWhiteListUrl}") String identityWhiteListUrl,
                           @Value("${identity.agencyTokenUrl}") String identityAgencyTokenUrl) {
        this.restOperations = restOperations;
        this.identityAPIUrl = identityAPIUrl;
        this.identityWhiteListUrl = identityWhiteListUrl;
        this.identityAgencyTokenUrl = identityAgencyTokenUrl;
    }

    public IdentityFromService findByEmail(String email) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(identityAPIUrl)
                .queryParam("emailAddress", email);

        log.debug(" Checking email {}", email);
        IdentityFromService identity;

        try {
            identity = restOperations.getForObject(builder.toUriString(), IdentityFromService.class);
        } catch (HttpClientErrorException http) {
            if (http.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null; // we kind of have to assume 403 is email not found rather than service not there ...
            }
            log.error(" Error with findByEmail when contacting identity service {}", builder.toUriString());
            return null;
        }

        return identity;
    }

    public String getEmailAddress(CivilServant civilServant) {

        log.debug("Getting email address for civil servant {}", civilServant);

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

    public boolean isDomainWhiteListed(String domain){
        log.debug("finding if domain:" + domain + " is whitelisted from identity service");

        String domainWithSlashAtStartAndEnd = "/" +  domain + "/";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(identityWhiteListUrl).path(domainWithSlashAtStartAndEnd);

        URI uri = null;
        try {
            uri = new URI(builder.toUriString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        }

        try {
            ResponseEntity<String> response = restOperations.getForEntity(uri, String.class);

            if(response.getStatusCode().is2xxSuccessful() && response.getBody().equals("true")) {
                return true;
            } else if(response.getStatusCode().is2xxSuccessful() && response.getBody().equals("false")) {
                return false;
            } else {
                throw new NoOrganisationsFoundException(domain);
            }
        } catch (Exception e) {
            log.warn("Error calling identity service", e);
            throw new NoOrganisationsFoundException(domain);
        }

    }

    public int getSpacesUsedForAgencyToken(String uid) throws CSRSApplicationException {
        log.debug("Getting the spaces used");
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(identityAgencyTokenUrl).path("/" + uid);

        try {
            return restOperations.getForObject(builder.toUriString(), Integer.class);
        } catch (HttpClientErrorException clientError) {
            if(clientError.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("Token for uid " + uid + " does not exist");
                throw new TokenDoesNotExistException(uid);
            } else {
                throw new CSRSApplicationException("Error calling identity service: get Agency Tokens Spaces Used", clientError);
            }
        } catch (HttpServerErrorException serverError) {
            throw new CSRSApplicationException("Server error calling identity service: get Agency Tokens Spaces Used", serverError);

        } catch (Exception e) {
            throw new CSRSApplicationException("Unexpected error calling identity service: get Agency Tokens Spaces Used", e);
        }

    }

}
