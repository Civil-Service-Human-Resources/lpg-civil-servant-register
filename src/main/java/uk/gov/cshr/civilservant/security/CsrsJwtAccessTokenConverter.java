package uk.gov.cshr.civilservant.security;

import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.intercept.RunAsUserToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.Identity;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;
import uk.gov.cshr.civilservant.repository.IdentityRepository;

import java.util.Map;
import java.util.Optional;

@Slf4j
public class CsrsJwtAccessTokenConverter extends JwtAccessTokenConverter {

    private static final String INTERNAL_ROLE = "INTERNAL";

    @Autowired
    private IdentityRepository identityRepository;

    @Autowired
    private CivilServantRepository civilServantRepository;

    public CsrsJwtAccessTokenConverter(IdentityRepository identityRepository, CivilServantRepository civilServantRepository) {
        this.identityRepository = identityRepository;
        this.civilServantRepository = civilServantRepository;
    }

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        OAuth2Authentication authentication = super.extractAuthentication(map);

        configureInternalUser();

        String identityId = (String) authentication.getPrincipal();

        Optional<Identity> identity = Optional.empty();

        try {
            identity = identityRepository.findByUid(identityId);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        Identity storedIdentity = identity.orElseGet(() -> {
            log.debug("No identity exists for id {}, creating.", identityId);
            Identity newIdentity = new Identity(identityId);
            return identityRepository.save(newIdentity);
        });

        Optional<CivilServant> civilServant = Optional.empty();

        try {
            civilServant = civilServantRepository.findByIdentity(storedIdentity);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        civilServant.orElseGet(() -> {
            log.debug("No civil servant exists for identity {}, creating.", storedIdentity);
            CivilServant newCivilServant = new CivilServant(storedIdentity);
            return civilServantRepository.save(newCivilServant);
        });

        return authentication;
    }

    private void configureInternalUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();

        securityContext.setAuthentication(new RunAsUserToken(INTERNAL_ROLE, null, null, ImmutableSet.of(new SimpleGrantedAuthority(INTERNAL_ROLE)), null));
    }
}
