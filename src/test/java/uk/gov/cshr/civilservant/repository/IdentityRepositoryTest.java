package uk.gov.cshr.civilservant.repository;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.Identity;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class IdentityRepositoryTest {

    @Autowired
    private IdentityRepository identityRepository;

    @Test
    public void shouldFindIdentityByExistingId() {

        final String uid = "uid";

        identityRepository.save(new Identity(uid));

        Optional<Identity> identity = identityRepository.findById(1L);

        assertTrue(identity.isPresent());
        Assert.assertThat(identity.get().getUid(), equalTo(uid));
    }

    @Test
    public void shouldNotFindIdentityByUnrecognisedId() {
        Optional<Identity> identity = identityRepository.findById(2L);
        assertFalse(identity.isPresent());
    }
}
