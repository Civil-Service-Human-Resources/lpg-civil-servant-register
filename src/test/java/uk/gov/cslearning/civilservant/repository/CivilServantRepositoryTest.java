package uk.gov.cslearning.civilservant.repository;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.civilservant.domain.CivilServant;
import uk.gov.cslearning.civilservant.domain.Identity;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CivilServantRepositoryTest {

    @Autowired
    private CivilServantRepository civilServantRepository;

    @Autowired
    private IdentityRepository identityRepository;

    @Test
    public void shouldFindCivilServantByIdentity() {

        final Identity identity = new Identity("uid");
        final CivilServant civilServant = new CivilServant(identity);

        identityRepository.save(identity);
        civilServantRepository.save(civilServant);

        Optional<CivilServant> organisation = civilServantRepository.findByIdentity(identity);
        assertTrue(organisation.isPresent());
    }

    @Test
    public void shouldNotFindCivilServantIfNotCreated() {

        final Identity identity = new Identity("uid");

        identityRepository.save(identity);

        Optional<CivilServant> organisation = civilServantRepository.findByIdentity(identity);
        assertFalse(organisation.isPresent());
    }
}
