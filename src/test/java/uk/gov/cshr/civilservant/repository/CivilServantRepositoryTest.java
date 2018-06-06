package uk.gov.cshr.civilservant.repository;


import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.intercept.RunAsUserToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.Identity;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CivilServantRepositoryTest {

    public static final String INTERNAL_ROLE = "INTERNAL";
    @Autowired
    private CivilServantRepository civilServantRepository;

    @Autowired
    private IdentityRepository identityRepository;

    @Before
    public void setUp() throws Exception {
        SecurityContext securityContext = SecurityContextHolder.getContext();

        securityContext.setAuthentication(new RunAsUserToken(INTERNAL_ROLE, null, null, ImmutableSet.of(new SimpleGrantedAuthority(INTERNAL_ROLE)), null));
    }

    @Test
    @WithMockUser(roles = INTERNAL_ROLE)
    public void shouldFindCivilServantByIdentity() {
        final Identity identity = new Identity("1");
        final CivilServant civilServant = new CivilServant(identity);

        identityRepository.save(identity);
        civilServantRepository.save(civilServant);

        Optional<CivilServant> organisation = civilServantRepository.findByIdentity(identity);
        assertTrue(organisation.isPresent());
    }

    @Test
    @WithMockUser(roles = INTERNAL_ROLE)
    public void shouldNotFindCivilServantIfNotCreated() {

        final Identity identity = new Identity("2");

        identityRepository.save(identity);

        Optional<CivilServant> organisation = civilServantRepository.findByIdentity(identity);
        assertFalse(organisation.isPresent());
    }
}
