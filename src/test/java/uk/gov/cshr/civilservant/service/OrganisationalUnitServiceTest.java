package uk.gov.cshr.civilservant.service;

import org.junit.Before;
import org.junit.Test;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;

import java.util.ArrayList;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

public class OrganisationalUnitServiceTest {

    private OrganisationalUnitService organisationalUnitService;

    @Before
    public void setup() {
        initMocks(this);
        organisationalUnitService = new OrganisationalUnitService();
    }

    @Test
    public void shouldReturnOrganisationalUnitsAsMap() {
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

        ArrayList<OrganisationalUnit> organisationalUnits = new ArrayList<>();
        organisationalUnits.add(parentOrganisationalUnit1);
        organisationalUnits.add(parentOrganisationalUnit2);
        organisationalUnits.add(childOrganisationalUnit);

        Map<String, String> organisationalUnitsMap = organisationalUnitService.getOrganisationalUnitsMap(organisationalUnits);

        assertThat(organisationalUnitsMap.size(), equalTo(3));
        assertThat(organisationalUnitsMap.get(parentOrganisationalUnit1.getCode()), equalTo("ParentTest1 (PT1)"));
        assertThat(organisationalUnitsMap.get(parentOrganisationalUnit2.getCode()), equalTo("ParentTest2 (PT2)"));
        assertThat(organisationalUnitsMap.get(childOrganisationalUnit.getCode()), equalTo("ParentTest1 (PT1), ChildTest1 (CT1)"));
    }

}