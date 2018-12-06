package uk.gov.cshr.civilservant.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cshr.civilservant.domain.Profession;
import uk.gov.cshr.civilservant.repository.ProfessionRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProfessionServiceTest {
    @Mock
    private ProfessionRepository professionRepository;

    @InjectMocks
    private ProfessionService professionService;

    @Test
    public void shouldReturnListOfParents() {
        Profession parent1 = new Profession();
        Profession child1 = new Profession();
        Profession child2 = new Profession();
        child1.setParent(parent1);
        child2.setParent(child1);

        Profession parent2 = new Profession();

        when(professionRepository.findAllByOrderByNameAsc()).thenReturn(Arrays.asList(parent1, child1, child2, parent2));

        List<Profession> result = professionService.getParents();

        assertEquals(Arrays.asList(parent1, parent2), result);
    }
}