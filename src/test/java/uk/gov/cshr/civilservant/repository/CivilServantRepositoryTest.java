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
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.domain.Profession;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CivilServantRepositoryTest {

    public static final String INTERNAL_ROLE = "INTERNAL";
    @Autowired
    private CivilServantRepository civilServantRepository;

    @Autowired
    private IdentityRepository identityRepository;

    @Autowired
    private OrganisationalUnitRepository organisationalUnitRepository;

    @Autowired
    private ProfessionRepository professionRepository;

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

        Optional<CivilServant> civilServantOptional = civilServantRepository.findByIdentity(identity);
        assertTrue(civilServantOptional.isPresent());
    }

    @Test
    @WithMockUser(roles = INTERNAL_ROLE)
    public void shouldNotFindCivilServantIfNotCreated() {

        final Identity identity = new Identity("2");

        identityRepository.save(identity);

        Optional<CivilServant> civilServant = civilServantRepository.findByIdentity(identity);
        assertFalse(civilServant.isPresent());
    }


    @Test
    public void shouldReturnCivilServantsByOrganisationalUnit() {
        OrganisationalUnit organisationalUnit1 = new OrganisationalUnit();
        organisationalUnit1.setName("Organisation1");
        organisationalUnit1.setCode("abc");

        organisationalUnitRepository.save(organisationalUnit1);

        OrganisationalUnit organisationalUnit2 = new OrganisationalUnit();
        organisationalUnit2.setName("Organisation2");
        organisationalUnit2.setCode("bcd");

        organisationalUnitRepository.save(organisationalUnit2);

        Identity identity1 = new Identity("1");
        CivilServant civilServant1 = new CivilServant(identity1);
        civilServant1.setOrganisationalUnit(organisationalUnit1);
        identityRepository.save(identity1);
        civilServantRepository.save(civilServant1);

        Identity identity2 = new Identity("2");
        CivilServant civilServant2 = new CivilServant(identity2);
        civilServant2.setOrganisationalUnit(organisationalUnit2);
        identityRepository.save(identity2);
        civilServantRepository.save(civilServant2);

        Identity identity3 = new Identity("3");
        CivilServant civilServant3 = new CivilServant(identity3);
        civilServant3.setOrganisationalUnit(organisationalUnit1);
        identityRepository.save(identity3);
        civilServantRepository.save(civilServant3);

        List<CivilServant> result = civilServantRepository.findAllByOrganisationalUnit(organisationalUnit1);

        assertEquals(2, result.size());
        assertEquals(civilServant1, result.get(0));
        assertEquals(civilServant3, result.get(1));
    }

    @Test
    public void shouldReturnCivilServantsByProfession() {
        Profession profession1 = new Profession("profession1");
        professionRepository.save(profession1);

        Profession profession2 = new Profession("profession2");
        professionRepository.save(profession2);

        Identity identity1 = new Identity("1");
        CivilServant civilServant1 = new CivilServant(identity1);
        civilServant1.setProfession(profession1);
        identityRepository.save(identity1);
        civilServantRepository.save(civilServant1);

        Identity identity2 = new Identity("2");
        CivilServant civilServant2 = new CivilServant(identity2);
        civilServant2.setProfession(profession2);
        identityRepository.save(identity2);
        civilServantRepository.save(civilServant2);

        Identity identity3 = new Identity("3");
        CivilServant civilServant3 = new CivilServant(identity3);
        civilServant3.setProfession(profession1);
        identityRepository.save(identity3);
        civilServantRepository.save(civilServant3);

        List<CivilServant> result = civilServantRepository.findAllByProfession(profession1);

        assertEquals(2, result.size());
        assertEquals(civilServant1, result.get(0));
        assertEquals(civilServant3, result.get(1));
    }
}
