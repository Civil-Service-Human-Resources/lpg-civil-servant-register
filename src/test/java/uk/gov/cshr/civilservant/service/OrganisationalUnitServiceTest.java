package uk.gov.cshr.civilservant.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.LinkBuilder;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.repository.OrganisationalUnitRepository;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class OrganisationalUnitServiceTest {
    @Mock
    private OrganisationalUnitRepository organisationalUnitRepository;

    @Mock
    private RepositoryEntityLinks repositoryEntityLinks;

    @InjectMocks
    private OrganisationalUnitService organisationalUnitService;

    @Test
    public void shouldReturnParentOrganisationalUnits() {
        ArrayList<OrganisationalUnit> organisationalUnits = createOrganisationalUnitStructure();

        when(organisationalUnitRepository.findAll()).thenReturn(organisationalUnits);

        List<OrganisationalUnit> parentOrganisationalUnits = organisationalUnitService.getParentOrganisationalUnits();

        assertThat(parentOrganisationalUnits.size(), equalTo(2));

        assertThat(parentOrganisationalUnits, containsInAnyOrder(
                hasProperty("code", equalTo("PT1")),
                hasProperty("code", equalTo("PT2"))
        ));
    }

    @Test
    public void shouldReturnOrganisationalUnitsAsMap() throws Exception {
        ArrayList<OrganisationalUnit> organisationalUnits = createOrganisationalUnitStructure();

        when(organisationalUnitRepository.findAll()).thenReturn(organisationalUnits);

        LinkBuilder linkBuilder = mock(LinkBuilder.class, Return.Self);
        LinkBuilder linkBuilder2 = mock(LinkBuilder.class, Return.Self);
        LinkBuilder linkBuilder3 = mock(LinkBuilder.class, Return.Self);
        LinkBuilder linkBuilder4 = mock(LinkBuilder.class, Return.Self);
        when(repositoryEntityLinks.linkFor(OrganisationalUnit.class)).thenReturn(linkBuilder);
        when(linkBuilder.slash(1L)).thenReturn(linkBuilder);
        when(linkBuilder.slash(2L)).thenReturn(linkBuilder2);
        when(linkBuilder.slash(3L)).thenReturn(linkBuilder3);
        when(linkBuilder.slash(4L)).thenReturn(linkBuilder4);
        when(linkBuilder.toUri()).thenReturn(new URI("PT1"));
        when(linkBuilder2.toUri()).thenReturn(new URI("PT2"));
        when(linkBuilder3.toUri()).thenReturn(new URI("CT1"));
        when(linkBuilder4.toUri()).thenReturn(new URI("GCT1"));

        Map<String, String> organisationalUnitsMap = organisationalUnitService.getOrganisationalUnitsMap();

        assertThat(organisationalUnitsMap.size(), equalTo(4));
        assertThat(organisationalUnitsMap.get("PT1"), equalTo("ParentTest1 (PT1)"));
        assertThat(organisationalUnitsMap.get("PT2"), equalTo("ParentTest2 (PT2)"));
        assertThat(organisationalUnitsMap.get("CT1"), equalTo("ParentTest1 (PT1) | ChildTest1"));
        assertThat(organisationalUnitsMap.get("GCT1"), equalTo("ParentTest1 (PT1) | ChildTest1 | GrandchildTest1 (GCT1)"));
    }

    private ArrayList<OrganisationalUnit> createOrganisationalUnitStructure() {
        OrganisationalUnit parentOrganisationalUnit1 = new OrganisationalUnit();
        parentOrganisationalUnit1.setName("ParentTest1");
        parentOrganisationalUnit1.setCode("PT1");
        parentOrganisationalUnit1.setAbbreviation("PT1");
        parentOrganisationalUnit1.setId(1L);

        OrganisationalUnit parentOrganisationalUnit2 = new OrganisationalUnit();
        parentOrganisationalUnit2.setName("ParentTest2");
        parentOrganisationalUnit2.setCode("PT2");
        parentOrganisationalUnit2.setAbbreviation("PT2");
        parentOrganisationalUnit2.setId(2L);

        OrganisationalUnit childOrganisationalUnit = new OrganisationalUnit();
        childOrganisationalUnit.setName("ChildTest1");
        childOrganisationalUnit.setCode("CT1");
        childOrganisationalUnit.setParent(parentOrganisationalUnit1);
        childOrganisationalUnit.setId(3L);

        OrganisationalUnit grandchildOrganisationalUnit = new OrganisationalUnit();
        grandchildOrganisationalUnit.setName("GrandchildTest1");
        grandchildOrganisationalUnit.setCode("GCT1");
        grandchildOrganisationalUnit.setAbbreviation("GCT1");
        grandchildOrganisationalUnit.setParent(childOrganisationalUnit);
        grandchildOrganisationalUnit.setId(4L);

        ArrayList<OrganisationalUnit> organisationalUnits = new ArrayList<>();
        organisationalUnits.add(parentOrganisationalUnit1);
        organisationalUnits.add(parentOrganisationalUnit2);
        organisationalUnits.add(childOrganisationalUnit);
        organisationalUnits.add(grandchildOrganisationalUnit);

        return organisationalUnits;
    }
}