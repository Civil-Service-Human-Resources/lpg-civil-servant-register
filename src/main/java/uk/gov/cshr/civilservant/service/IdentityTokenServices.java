package uk.gov.cshr.civilservant.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.stereotype.Service;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.Identity;
import uk.gov.cshr.civilservant.repository.IdentityRepository;
import uk.gov.cshr.civilservant.repository.InternalCivilServantRepository;

import java.util.Optional;

@Service
public class IdentityTokenServices extends RemoteTokenServices {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdentityTokenServices.class);


    private InternalCivilServantRepository civilServantRepository;

    private IdentityRepository identityRepository;

    @Autowired
    public IdentityTokenServices(InternalCivilServantRepository civilServantRepository,
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
    public OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {

        OAuth2Authentication authentication = super.loadAuthentication(accessToken);

        String identityUid = (String) authentication.getPrincipal();

        Optional<Identity> identity = identityRepository.findByUid(identityUid);

        Identity storedIdentity = identity.orElseGet(() -> {
            LOGGER.debug("No identity exists for uid {}, creating.", identityUid);
            Identity newIdentity = new Identity(identityUid);
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
}
