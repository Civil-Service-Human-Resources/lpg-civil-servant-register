package uk.gov.cshr.civilservant.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.repository.OrganisationalUnitRepository;

import java.util.ArrayList;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OrganisationalUnitServiceTest {

    private OrganisationalUnitService organisationalUnitService;

    @Mock
    private OrganisationalUnitRepository organisationalUnitRepository;

    @Before
    public void setup() {
        initMocks(this);
        organisationalUnitService = new OrganisationalUnitService(organisationalUnitRepository);
    }

    @Test
    public void shouldReturnParentOrganisationalUnits() {
        ArrayList<OrganisationalUnit> organisationalUnits = createOrganisationalUnitStructure();

        when(organisationalUnitRepository.findAll()).thenReturn(organisationalUnits);

        ArrayList<OrganisationalUnit> parentOrganisationalUnits = organisationalUnitService.getParentOrganisationalUnits();

        assertThat(parentOrganisationalUnits.size(), equalTo(2));

        assertThat(parentOrganisationalUnits, containsInAnyOrder(
                hasProperty("code", equalTo("PT1")),
                hasProperty("code", equalTo("PT2"))
        ));
    }

    @Test
    public void shouldReturnOrganisationalUnitsAsMap() {
        ArrayList<OrganisationalUnit> organisationalUnits = createOrganisationalUnitStructure();

        when(organisationalUnitRepository.findAll()).thenReturn(organisationalUnits);

        Map<String, String> organisationalUnitsMap = organisationalUnitService.getOrganisationalUnitsMap();

        assertThat(organisationalUnitsMap.size(), equalTo(4));
        assertThat(organisationalUnitsMap.get("PT1"), equalTo("ParentTest1 (PT1)"));
        assertThat(organisationalUnitsMap.get("PT2"), equalTo("ParentTest2 (PT2)"));
        assertThat(organisationalUnitsMap.get("CT1"), equalTo("ParentTest1 (PT1), ChildTest1 (CT1)"));
        assertThat(organisationalUnitsMap.get("GCT1"), equalTo("ParentTest1 (PT1), ChildTest1 (CT1), GrandchildTest1 (GCT1)"));
    }

    private ArrayList<OrganisationalUnit> createOrganisationalUnitStructure() {
        OrganisationalUnit parentOrganisationalUnit1 = new OrganisationalUnit();
        parentOrganisationalUnit1.setName("ParentTest1");
        parentOrganisationalUnit1.setCode("PT1");
        parentOrganisationalUnit1.setAbbreviation("PT1");

        OrganisationalUnit parentOrganisationalUnit2 = new OrganisationalUnit();
        parentOrganisationalUnit2.setName("ParentTest2");
        parentOrganisationalUnit2.setCode("PT2");
        parentOrganisationalUnit2.setAbbreviation("PT2");

        OrganisationalUnit childOrganisationalUnit = new OrganisationalUnit();
        childOrganisationalUnit.setName("ChildTest1");
        childOrganisationalUnit.setCode("CT1");
        childOrganisationalUnit.setAbbreviation("CT1");
        childOrganisationalUnit.setParent(parentOrganisationalUnit1);

        OrganisationalUnit grandchildOrganisationalUnit = new OrganisationalUnit();
        grandchildOrganisationalUnit.setName("GrandchildTest1");
        grandchildOrganisationalUnit.setCode("GCT1");
        grandchildOrganisationalUnit.setAbbreviation("GCT1");
        grandchildOrganisationalUnit.setParent(childOrganisationalUnit);

        ArrayList<OrganisationalUnit> organisationalUnits = new ArrayList<>();
        organisationalUnits.add(parentOrganisationalUnit1);
        organisationalUnits.add(parentOrganisationalUnit2);
        organisationalUnits.add(childOrganisationalUnit);
        organisationalUnits.add(grandchildOrganisationalUnit);

        return organisationalUnits;
    }
}