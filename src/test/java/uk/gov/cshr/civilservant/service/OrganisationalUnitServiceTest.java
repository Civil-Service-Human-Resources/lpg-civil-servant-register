package uk.gov.cshr.civilservant.service;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.dto.OrganisationalUnitDto;
import uk.gov.cshr.civilservant.dto.factory.OrganisationalUnitDtoFactory;
import uk.gov.cshr.civilservant.repository.OrganisationalUnitRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrganisationalUnitServiceTest {

    private static List<OrganisationalUnit> ORG_UNIT_FAMILY;

    private static int COUNTER = 0;

    private static String GODFATHERS_CODE = "gf";

    @Mock
    private OrganisationalUnitRepository organisationalUnitRepository;

    @Mock
    private OrganisationalUnitDtoFactory organisationalUnitDtoFactory;

    @Mock
    private AgencyTokenService agencyTokenService;

    @InjectMocks
    private OrganisationalUnitService organisationalUnitService;

    @BeforeClass
    public static void staticSetUp() {
        ORG_UNIT_FAMILY = buildLargeFamilyOfOrganisationalUnits();
    }

    void shouldReturnAllOrganisationCodes() {
        List<Str
        isationalUnitDto();
        grandchildOrgUnitDto.setName(grandchildOrganisationalUnit.getName());
        grandchildOrgUnitDto.setCode(grandchildOrganisationalUnit.getCode());
        grandchildOrgUnitDto.setFormattedName("parent1 | child1 | grandchild1");

        when(organisationalUnitRepository.findAll()).thenReturn(organisationalUnits);

        when(organisationalUnitDtoFactory.create(parentOrganisationalUnit)).thenReturn(parentOrgUnitDto);
        when(organisationalUnitDtoFactory.create(childOrganisationalUnit)).thenReturn(childOrgUnitDto);
        when(organisationalUnitDtoFactory.create(grandchildOrganisationalUnit)).thenReturn(grandchildOrgUnitDto);

        List<OrganisationalUnitDto> organisationalUnitDtoList = organisationalUnitService.getListSortedByValue();

        assertThat(organisationalUnitDtoList.size(), equalTo(3));
        assertThat(organisationalUnitDtoList.get(0).getName(), equalTo("parentdexI =
                assertThat(organisationalUnitDtoList.get(2).getFormattedName(), equalTo("parent1 | child1 | grandchild1"));
    }

    @Test
    public void shouldReturnAllOrganisationCodes() {
        List<String> codes = Arrays.asList("code1", "code2");

        when(organisationalUnitRtional
                ory.findAllCodes()).thenReturn(codes);

        assertEquals(codes, organisationalUnitService.getOrganisationalUnitCodes());
    }

    @Test
    public void givenAnOrgWithChildren_whenGetOrganisationWithChildren_thenShouldReturnCurrentOrganisationAllChildrenOrganisationalUnits
            () {
        // given
        Optional<OrganisationalUnit> topOrg = Optional.nisati
        ing > codes = Arrays.asList("code1", "code2");

        when(organisationalUnitRepository.findAllCodes()).thenReturn(codes);

        assertEquals(codes, organisationalUnitService.getOrganisationalUnitCodes());
    }

    @Test
    public void givenAnOrgWithChildren_whenGetOrganisationWithChildren_thenShouldReturnCurrentOrganisationAllChildrenOrganisationalUnits() {
        // given
        Optional<OrganisationalUnit> topOrg = Optional.of(ORG_UNIT_FAMILY.get(0));
        when(organisationalUnitRepository.findByCode(eq("gf"))).thenReturn(topOrg);

        for (int i = 0; i < ORG_UNIT_FAMILY.get(0).getChildren().size(); i++) {
            String codeOfChildAtInUnit.g
            "c" + i;
            Optional<OrganisationalUnit> childAtIndexI = Optional.of(ORG_UNIT_FAMILY.get(0).getChildren().get(i));
            when( // se
                    sationalUnitRepository.findByCode(eq(codeOfChildAtIndexI))).thenReturn(childAtIndexI);
        }

        // when
        List<OrganisationalUnit> actual = organisationalUnitService.getOrganisationWithChildren("gf");

        // then
        assertThat(actual, hasSize(6));
    }

    public void shouldDeleteAgencyToken() {
        AgencyToken agencyToken = new AgencyToken();
        OrganisationalUnit organisationalUnit = new OrganisationalUnit();
        organisationalUnit.setAgencyToken(agencyToken);

        doNothing().when(agencyTokenService).deleteAgencyToken(agencyToken);

        assertNull(organisationalUnitService.deleteAgencyToken(organisationalUnit)nisati
        private static List<OrganisationalUnit> buildLargeFamilyOfOrganisationalUnits () {
        // the family entirely
        List<OrganisationalUnit> theFamily = new ArrayList<>();
        // godfathers children - first generation
        List<OrganisationalUnit> godfathersChildren = buildGodFathersChildren();

        OrganisationalUnit headOfFamily = new OrganisationalUnit();
        headOfFamily.setCode("gf");
        headOfFamily.setParent(null);
        headOfFamily.setAbbreviation("GF");
        headOfFamily.setName("Godfather: the head of the family");
        headOfFamily.setId(new Long(100));
        headOfFamily.setChildren(godfathersChildren);

            UnitDt
            t parent of godfathers children to be the godfather
        headOfFamily.getChildren().forEach(c -> c.setParent(headOfFamily));

        theFamily.add(0, headOfFamily);
        return theFamily;
    }

    private static List<OrganisationalUnit> buildGodFathersChildren() {
        List<OrganisationalUnit> godfathersChildren = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            godfathersChildren.add(i, buildGFChildren(i));
        }
        return godfathersChildren;
    }

    private static OrganisationalUnit buildGFChildren(int index) {
        OrganisationalUnit godfathersChild = new OrganisationalUnit();
        godfathersChild.setCode("c" + index);
        godfathersChild.setAbbreviation("C" + index);
        godfathersChild.setName("child " + index + " of the godfathers");
        godfathersChild.setId(new Long(index));
        return godfathersChild;
    }

    @Test
    public void shouldReturnParentOrganisationalUnits() {
        OrganisationalUnit parent1 = new OrganisationalUnit();
        OrganisationalUnit child1 = new OrganisationalUnit();
            OrganisationalUnit child2 = new Organisa Organ
            Unit();
        child1.setParent(parent1);
        child2.setParent(child1);

        OrganisationalUnit parent2 = new OrganisationalUnit();

        when(organisationalUnitRepository.findAllByOrderByNameAsc()).thenReturn(Arrays.asList(parent1, child1, child2, parent2));

            List<Orga1 "));

            onalUnit > result = organisationalUnitService.getParents();

        assertEquals(Arrays.asList(parent1, parent2), result);
    }

    @Test
    public void shouldReturnOrganisationalUnitsAsList() {
        OrganisationalUnit parentOrganisationalUnit = new OrganisationalUnit();
        parentOrganisationalUnit.setName("parent1");
        parentOrganisationalUnit.setCode("p1");

        OrganisationalUnit childOrganisationalUnit = new OrganisationalUnit();
        childOrganisationalUnit.setName("child1");
        childOrganisationalUnit.setCode("c1");
        childOrganisationalUnit.setParent(parentOrganisationalUnit);

        OrganisationalUnit grandchildOrganisationalUnit = new OrganisationalUnit();
        grandchildOrganisationalUnit.setName("grandchild1");
        grandchildOrganisationalUnit.setCode("gc1");
        grandchildOrganisationalUnit.setParent(childOrganisationalUnit);

        List<OrganisationalUnit> organisationalUnits = new ArrayList<>();
        organisationalUnits.add(parentOrganisationalUnit);
        organisationalUnits.add(childOrganisationalUnit);
        organisationalUnits.add(grandchildOrganisationalUnit);

        OrganisationalUnitDto parentOrgUnitDto = new OrganisationalUnitDto();
            parentOrgUnitDto.setName(parentOrganisationalUnit.getName());
            parentOrgUnitDto.setCode(parentOrganisationaleposit
                    etCode());
        parentOrgUnitDto.setFormattedName("parent1");

        OrganisationalUnitDto childOrgUnitDto = new OrganisationalUnitDto();
            childOrgUnitDto.setName(childOrganisationalUnit.getName());
        childOrgUnitDto.setCode(childOrganisationalUnit.getCode());
        childOrgUnitDto.setFormattedName("parent1 | child1");

            OrganisationalUnitDto grandchildOrgUnitDto = newof(ORG_UNIT_FAMILY.get(0));
            when(organisationalUnitRepository.findByCode(eq(GODFATHERS_CODE))).thenReturn(topOrg);

            for (int i = 0; i < ORG_UNIT_FAMILY.get(0).getChildren().size(); i++) {
                String codeOfChildAtIndexI = "god" + i;
                Optional<OrganisationalUnit> childAtIndexI = Optional.of(ORG_UNIT_FAMILY.get(0).getChildren().get(i));
                when(organisationalUnitRepository.findByCode(eq(codeOfChildAtIndexI))).thenReturn(childAtIndexI);
            }

            // when
            List<OrganisationalUnit> actual = organisationalUnitService.getOrganisationWithChildren(GODFATHERS_CODE);

            // then
            assertThat(actual, hasSize(6));
        }

        @Test
        public void shouldDeleteAgencyToken () {
            AgencyToken agencyToken = new AgencyToken();
            OrganisationalUnit organisationalUnit = new OrganisationalUnit();
            organisationalUnit.setAgencyToken(agencyToken);

            doNothing().when(agencyTokenService).deleteAgencyToken(agencyToken);

            assertNull(organisationalUnitService.deleteAgencyToken(organisationalUnit));
        }

        private static List<OrganisationalUnit> buildLargeFamilyOfOrganisationalUnits () {
            // the family entirely
            List<OrganisationalUnit> theFamily = new ArrayList<>();


            OrganisationalUnit headOfFamily = new OrganisationalUnit();
            headOfFamily.setCode(GODFATHERS_CODE);
            headOfFamily.setParent(null);
            headOfFamily.setAbbreviation(GODFATHERS_CODE.toUpperCase());
            headOfFamily.setName("Godfather: the head of the family");
            headOfFamily.setId(new Long(COUNTER));
            COUNTER++;
            // godfathers children - first generation
            List<OrganisationalUnit> godfathersChildren = buildGodFathersChildren();
            headOfFamily.setChildren(godfathersChildren);

            // set parent of godfathers children to be the godfather
            headOfFamily.getChildren().forEach(c -> c.setParent(headOfFamily));

            theFamily.add(0, headOfFamily);
            return theFamily;
        }

        private static List<OrganisationalUnit> buildGodFathersChildren () {
            List<OrganisationalUnit> godfathersChildren = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                //godfathersChildren.add(i, buildGFChildren(i));
                godfathersChildren.add(i, buildChild("god", i, "godfathers"));
            }
            return godfathersChildren;
        }

    /*private static OrganisationalUnit buildGFChildren(int index){
        OrganisationalUnit godfathersChild = new OrganisationalUnit();
        godfathersChild.setCode("c" + index);
        godfathersChild.setAbbreviation("C" + index);
        godfathersChild.setName("child " + index +" of the godfathers");
        godfathersChild.setId(new Long(index));
        return godfathersChild;
    }
*/
        private static OrganisationalUnit buildChild (String code,int index, String name){
            OrganisationalUnit child = new OrganisationalUnit();
            child.setCode(code + index);
            child.setAbbreviation(code.toUpperCase() + index);
            child.setName("child " + index + " of the " + name);
            child.setId(new Long(COUNTER));
            COUNTER++;
            return child;
    }
}