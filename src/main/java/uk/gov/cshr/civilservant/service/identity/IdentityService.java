package uk.gov.cshr.civilservant.service.identity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.dto.IdentityAgencyResponseDTO;
import uk.gov.cshr.civilservant.exception.CSRSApplicationException;
import uk.gov.cshr.civilservant.exception.NoOrganisationsFoundException;
import uk.gov.cshr.civilservant.exception.TokenDoesNotExistException;
import uk.gov.cshr.civilservant.service.exception.UserNotFoundException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Slf4j
@Service
public class IdentityService {

    private OAuth2RestOperations restOperations;

    private String identityAPIUrl;

    private String identityWhiteListUrl;

    private String agencyTokenUrl;

    private String identityAgencyTokenUrl;

    private final UriComponentsBuilder agencyTokenUrlBuilder;

    @Autowired
    public IdentityService(OAuth2RestOperations restOperations, @Value("${identity.identityAPIUrl}") String identityAPIUrl,
                           @Value("${identity.identityWhiteListUrl}") String identityWhiteListUrl,
                           @Value("${identity.agencyTokenUrl}") String agencyTokenUrl,
                           @Value("${identity.identityAgencyTokenUrl}") String identityAgencyTokenUrl) {
        this.restOperations = restOperations;
        this.identityAPIUrl = identityAPIUrl;
        this.identityWhiteListUrl = identityWhiteListUrl;
        this.agencyTokenUrl = agencyTokenUrl;
        this.identityAgencyTokenUrl = identityAgencyTokenUrl;
        this.agencyTokenUrlBuilder = UriComponentsBuilder.fromHttpUrl(agencyTokenUrl);
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

    public Optional<String> getAgencyTokenUid(String userUid) throws CSRSApplicationException {
        log.debug("Getting the agency token uid from identity service");
        StringBuilder sb = new StringBuilder(identityAgencyTokenUrl);
        sb.append(userUid);
     /*   UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(identityAgencyTokenUrl).path(userUid);
        //UriComponents url = identityAgencyTokenUrlBuilder.path(userUid).build();

        URI uri;
        try {
            uri = new URI(builder.toUriString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return Optional.empty();
        }*/

        try {
            ResponseEntity<IdentityAgencyResponseDTO> response = restOperations.getForEntity(sb.toString(), IdentityAgencyResponseDTO.class);

            if(response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().getAgencyTokenUid() != null) {
                return Optional.of(response.getBody().getAgencyTokenUid());
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Unexpected error calling identity service: get agency token uid", e);
            throw new CSRSApplicationException("Unexpected error calling identity service: get agency token uid", e);
        }

    }

    public int getSpacesUsedForAgencyToken(String uid) throws CSRSApplicationException {
        log.debug("Getting the spaces used");
        UriComponents url = agencyTokenUrlBuilder.buildAndExpand(uid);

        try {
            return restOperations.getForObject(url.toUriString(), Integer.class);
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
/*
Caused by: org.springframework.web.client.RestClientException: Could not extract response:
no suitable HttpMessageConverter found for response type [class uk.gov.cshr.civilservant.dto.IdentityAgencyResponseDTO] and content type [text/html;charset=UTF-8]
 */
    /*public Optional<String> getAgencyTokenUidWontWork(String userUid) throws CSRSApplicationException {
        log.debug("Getting the agency token uid from identity service");
        UriComponents url = identityAgencyTokenUrlBuilder.buildAndExpand(userUid);

        try {
            //ResponseEntity<IdentityAgencyResponseDTO> responseEntity = restOperations.getForEntity(url.toUri(), IdentityAgencyResponseDTO.class);
          //  Map<String, Object> request = new HashMap<String, Object>(2);
           // request.put("name", name.getName());
            Map<String, Object> request = new HashMap<String, Object>(1);
            //request.put(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8");
            //"text/html;charset=utf-8";
            request.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            ResponseEntity<IdentityAgencyResponseDTO> responseEntity = restOperations.getForEntity(url.toUriString(), IdentityAgencyResponseDTO.class, request);
            //ResponseEntity<IdentityAgencyResponseDTO> responseEntity = restOperations.getForEntity(url.toUri(), IdentityAgencyResponseDTO.class, new HttpEntity<Map<String, Object>>(request));


            if(responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null && responseEntity.getBody().getAgencyTokenUid() != null) {
                return Optional.of(responseEntity.getBody().getAgencyTokenUid());
            } else {
                return Optional.empty();
            }
        } catch(RestClientException clientError) {
            log.error("Error calling identity service: get agency token uid", clientError);
            throw new CSRSApplicationException("Error calling identity service: get agency token uid", clientError);
            *//*catch (HttpClientErrorException clientError) {
            log.error("Error calling identity service: get agency token uid", clientError);
            throw new CSRSApplicationException("Error calling identity service: get agency token uid", clientError);
        } catch (HttpServerErrorException serverError) {
            log.error("Server error calling identity service: get agency token uid", serverError);
            throw new CSRSApplicationException("Server error calling identity service: get agency token uid", serverError);
        } *//*
        } catch (Exception e) {
            log.error("Unexpected error calling identity service: get agency token uid", e);
            throw new CSRSApplicationException("Unexpected error calling identity service: get agency token uid", e);
        }
    }*/

    public void removeAgencyTokenFromUsers(String agencyTokenUid) throws CSRSApplicationException {
        log.debug("Removing agency token");
        UriComponents url = agencyTokenUrlBuilder.buildAndExpand(agencyTokenUid);

        try {
            restOperations.delete(url.toUriString());
        } catch (HttpClientErrorException clientError) {
            throw new CSRSApplicationException("Error calling identity service: delete agency token", clientError);
        } catch (HttpServerErrorException serverError) {
            throw new CSRSApplicationException("Server error calling identity service: delete agency token", serverError);
        } catch (Exception e) {
            throw new CSRSApplicationException("Unexpected error calling identity service: delete agency token", e);
        }
    }

}
