package uk.gov.cshr.civilservant.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.Profession;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
@WithMockUser(authorities = {"PROFESSION_MANAGER"})
public class ProfessionRepositoryTest {

    @Autowired
    private ProfessionRepository professionRepository;

    @Test
    @Rollback(false)
    public void shouldReturnProfessionAndParent() {
        Profession parent = new Profession();
        parent.setName("parent");

        Profession child = new Profession();
        child.setName("child");

        child.setParent(parent);

        Profession savedChild = professionRepository.save(child);

        Optional<Profession> optional = professionRepository.findById(savedChild.getId());

        assertTrue(optional.isPresent());

        assertEquals(parent, optional.get().getParent());
    }
}