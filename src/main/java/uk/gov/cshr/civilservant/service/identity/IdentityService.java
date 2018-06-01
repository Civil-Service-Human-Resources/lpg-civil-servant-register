package uk.gov.cshr.civilservant.service.identity;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;
import java.util.HashMap;


import static java.util.Collections.emptySet;

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

    public OAuth2AccessToken getAccessToken() {
        return restOperations.getAccessToken();
    }

    public Collection<IdentityFromService> listAll() {
        LOGGER.debug("Retrieving all identities");
        IdentityFromService[] identities = restOperations.getForObject(identityAPIUrl, IdentityFromService[].class);
        if (identities != null) {
            return Sets.newHashSet(identities);
        }
        return emptySet();
    }

    public IdentityFromService findByEmail(String email) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(identityAPIUrl)
                .queryParam("emailAddress", email);

        LOGGER.debug(" Checking email {}", email);
        IdentityFromService identity;

        try {
            identity = restOperations.getForObject(builder.toUriString(), IdentityFromService.class);
        } catch (HttpClientErrorException http) {
            return null;
        }

        return identity;
    }
}
