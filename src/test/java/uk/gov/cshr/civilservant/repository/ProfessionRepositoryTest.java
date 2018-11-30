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

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
@WithMockUser(username = "user",  roles = {"PROFESSION_MANAGER"})
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

    @Test
    public void shouldReturnProfessionAndChildren() {
        Profession parent = new Profession();
        parent.setName("parent");

        Profession child1 = new Profession();
        child1.setName("child1");

        Profession child2 = new Profession();
        child2.setName("child2");

        parent.setChildren(Arrays.asList(child1, child2));

        Profession savedParent = professionRepository.save(parent);

        Optional<Profession> optional = professionRepository.findById(savedParent.getId());

        assertTrue(optional.isPresent());
        assertEquals(Arrays.asList(child1, child2), savedParent.getChildren());

        assertTrue(professionRepository.findById(savedParent.getChildren().get(0).getId()).isPresent());
    }
}