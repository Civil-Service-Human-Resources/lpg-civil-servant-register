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
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProfessionServiceTest {
    @Mock
    private ProfessionRepository professionRepository;

    @Mock
    private RepositoryEntityService<Profession> repositoryEntityService;

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

        List<Profession> result = professionService.getParentProfessions();

        assertEquals(Arrays.asList(parent1, parent2), result);
    }

    @Test
    public void shouldReturnOrganisationalUnitsAsMap() {
        Profession parent1 = new Profession("Parent One");
        Profession child1 = new Profession("Child One");
        Profession child2 = new Profession("Child Two");
        child1.setParent(parent1);
        child2.setParent(child1);

        Profession parent2 = new Profession("Parent Two");

        when(professionRepository.findAll()).thenReturn(Arrays.asList(parent1, child1, child2, parent2));

        when(repositoryEntityService.getUri(Profession.class, parent1)).thenReturn("parent1");
        when(repositoryEntityService.getUri(Profession.class, child1)).thenReturn("child1");
        when(repositoryEntityService.getUri(Profession.class, child2)).thenReturn("child2");
        when(repositoryEntityService.getUri(Profession.class, parent2)).thenReturn("parent2");

        Map<String, String> professionsMap = professionService.getProfessionsMapSortedByValue();

        assertThat(professionsMap.size(), equalTo(4));
        assertThat(professionsMap.get("parent1"), equalTo("Parent One"));
        assertThat(professionsMap.get("child1"), equalTo("Parent One | Child One"));
        assertThat(professionsMap.get("child2"), equalTo("Parent One | Child One | Child Two"));
        assertThat(professionsMap.get("parent2"), equalTo("Parent Two"));
    }
}