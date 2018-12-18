package uk.gov.cshr.civilservant.service;

import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.intercept.RunAsUserToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.stereotype.Service;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.Identity;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;
import uk.gov.cshr.civilservant.repository.IdentityRepository;

import java.util.Optional;

@Service
public class IdentityTokenServices extends RemoteTokenServices {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdentityTokenServices.class);
    public static final String INTERNAL_ROLE = "INTERNAL";

    private CivilServantRepository civilServantRepository;

    private IdentityRepository identityRepository;

    @Autowired
    public IdentityTokenServices(CivilServantRepository civilServantRepository,
                                 IdentityRepository identityRepository,
                                 @Value("${oauth.clientId}") String clientId,
                                 @Value("${oauth.clientSecret}") String clientSecret,
                                 @Value("${oauth.checkTokenEndpointUrl}") String checkTokenEndpointUrl) {
        this.civilServantRepository = civilServantRepository;
        this.identityRepository = identityRepository;
        setClientId(clientId);
        setClientSecret(clientSecret);
        setCheckTokenEndpointUrl(checkTokenEndpointUrl);
    }

    @Override
    public OAuth2Authentication loadAuthentication(String accessToken) {
        configureInternalUser();

        OAuth2Authentication authentication = super.loadAuthentication(accessToken);

        String identityId = (String) authentication.getPrincipal();

        Optional<Identity> identity = identityRepository.findByUid(identityId);

        Identity storedIdentity = identity.orElseGet(() -> {
            LOGGER.debug("No identity exists for id {}, creating.", identityId);
            Identity newIdentity = new Identity(identityId);
            return identityRepository.save(newIdentity);
        });

        Optional<CivilServant> civilServant = civilServantRepository.findByIdentity(storedIdentity);

        civilServant.orElseGet(() -> {
            LOGGER.debug("No civil servant exists for identity {}, creating.", identity);
            CivilServant newCivilServant = new CivilServant(storedIdentity);
            return civilServantRepository.save(newCivilServant);
        });

        return authentication;
    }

    /*
     * Max/Matt - This is to allow our internal system to call methods we are exposing through Spring Data Rest interface.
     * We have authorisation on these functions to limit what external users can do,
     * so here we are masquerading as an external user to allow our internal system to also use this functionality.
     */
    private void configureInternalUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();

        securityContext.setAuthentication(new RunAsUserToken(INTERNAL_ROLE, null, null, ImmutableSet.of(new SimpleGrantedAuthority(INTERNAL_ROLE)), null));
    }
}
