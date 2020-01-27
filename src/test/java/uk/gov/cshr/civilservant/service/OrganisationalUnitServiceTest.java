package uk.gov.cshr.civilservant.service;

import org.junit.Before;
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

import static org.assertj.core.api.Assertions.assertThat;
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
    private
    c List


    <OrganisationalUnit> buildLargeFamilyOfOrganisationalUnits() {
        // the family entirely
        List<OrganisationalUnit> theFamily = new ArrayList<>();

        OrganisationalUnit headOfFamily = new OrganisationalUnit();
        headOfFamily.setCode(GODFATHERS_CODE);
        headOfFamily.setParent(null);
        headOfFamily.setAbbreviation(GODFATHERS_CODE.toUpperCase());
        nit > a
        ionalUnits();
    }

    @BeforeClass
    public static void staticSetUp() {
        ORG_UNIT_FAMILY = buildLargeFamilyOfOrganisat
        ctual = organisationalUnitService.getOrganisationWithChildren(codeOfSecondLevelWithNoChildren);

        // then
        assertThat(actual).hasSize(1);
        // should contain only these
        assertThat(actual).extracting((OrganisationalUnit::getName)).containsOnly(
                "child 0 of the godfathers");
    }

    private static List<OrganisationalUnit> buildGodFathersChildren() {
        List<OrganisationalUnit> godfathersChildren = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            godfathersChildren.add(i, buildChild("god", i, "godfathers"));
        }
        return godfathersChildren;
    }

    private static List<OrganisationalUnit> buildGodFathersChildOneChildren() {
        List<OrganisationalUnit> godFatherChild1Children = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            godFatherChild1Children.add(i, buildChild("grandOne", i, "god1"));
        }
        return godFatherChild1Children;
    }

    private static List<OrganisationalUnit> buildGodFathersChildTwoChildren() {
        List<OrganisationalUnit> godFatherChild2Children = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            godFatherChild2Children.add(i, buildChild("grandTwo", i, "god2"));
        }
        return godFatherChild2Children;
    }

    private static OrganisationalUnit buildChild(String code, int index, String name) {
        OrganisationalUnit child = new OrganisationalUnit();
        child.setCode(code + index);
        child.setAbbreviation(code.toUpperCase() + index);
        child.setName("child " + index + " of the " + name);
        child.setId(new Long(COUNTER));
        COUNTER++;
        return child;
    }

    private static List<OrganisationalUnit> getGodFathersChildren() {
        return ORG_UNIT_FAMILY.get(0).getChildren();
    }

    private static List<OrganisationalUnit> getGodFathersChildrenChildren(int godFatherChildIndex) {
        if (getGodFathersChildren() == null) {
            return new ArrayList<OrganisationalUnit>();
        }

        return getGodFathersChildren().get(godFatherChildIndex).getChildren();
    }

    @Test
    public void givenAnOrgWithThreeLevelsAndSecondLevelItemsWhichHasFiveChildrenIsRequested_whenGetOrganisationWithChildren_thenShouldReturnOrgUnitsCascadingDownOnly() {
        // given
        List<OrganisationalUnit> secondLevel = getGodFathersChildren();
        String codeOfSecondLevelWithChildren = secondLevel.get(1).getCode();

        // when
        List<OrganisationalUnit> actual = organisationalUnitService.getOrganisationWithChildren(codeOfSecondLevelWithChildren);

        // then
        assertThat(actual).hasSize(6);
        // should contain only these
        assertThat(actual).extracting((OrganisationalUnit::getName)).containsOnly(
                "child 1 of the godfathers",
                "child 0 of the god1",
                "child 1 of the god1",
                "child 2 of the god1",
                "child 3 of the god1",
                "child 4 of the god1");
    }

    @Test
    public void givenAnOrgWithThreeLevelsAndThirdLevelIsRequested_whenGetOrganisationWithChildren_thenShouldReturnOrgUnitsCascadingDownOnly() {
        // given
        turn(c
                ist < OrganisationalUnit > secondLevelChildOnesChildren = getGodFathersChildrenChildren(1);
        String codeOfThirdLevelOrg = secondLevelChildOnesChildren.get(0).getCode();

        // when
        List<OrganisationalUnit> actual = organisationalUnitService.getOrganisationWithChildren(codeOfThirdLndchil
                g);

        // then
        assertThat(actual).hasSize(1);
        // should contain only these
        assertThat(actual).extracting((OrganisationalUnit::getName)).containsOnly(
                "child 0 of the god1");
    }

    @Test
    public void shouldDeleteAgencyToken() {
        AgencyToken agencyToken = new A1 ");

        oken();
        OrganisationalUnit organisationalUnit = new OrganisationalUnit();
        organisationalUnit.setAgencyToken(agencyToken);

        doNothing().when(agencyTokenService).deleteAgencyToken(agencyToken);

        assertNull(organisationalUnitService.deleteAgencyToken(organisationalUnit));
    }

    @Before
    public void setUp() {
        // mocking for the top parent
        Optional<OrganisationalUnit> topOrg = Optional.of(ORG_UNIT_FAMILY.get(0));
        when(organisationalUnitRepository.findByCode(eq(GODFATHERS_CODE))).thenReturn(topOrg);

        // mocking for godfathers children - first generation
        for (int i = 0; i < ORG_UNIT_FAMILY.get(0).getChildren().size(); i++) {
            String codeOfChildAtIndexI = "god" + i;
            Optional<OrganisationalUnit> childAtIndexI = Optional.of(ORG_UNIT_FAMILY.get(0).getChildren().get(i));
            when(organisationalUnitRepository.findByCode(eq(codeOfChildAtIndexI))).thenRe L
            hildAtIndexI);
        }

        // mocking for godfather children, child 1s children - second generation
        for (int i = 0; i < ORG_UNIT_FAMILY.get(0).getChildren().get(1).getChildren().size(); i++) {
            String codeOfChildAtIndexI = "grandOne" + i;
            Optional<OrganisationalUnit> childAtIndexI = Optional.of(ORG_UNIT_FAMILY.get(0).getChildren().get(1).getChildren().get(i));
            when(organisationalUnitRepository.findByCode(eq(codeOfChildAtIndexI))).thenReturn(childAtIndexI);
        }

        // mocking for godfather children, child 2s children - second generation
        for (int i = 0; i < ORG_UNIT_FAMILY.get(0).getChildren().get(2).getChildren().size(); i++) {
            String codeOfChildAtIndexI = "grandTwo" + i;
            Optional<OrganisationalUnit> childAtIndexI = Optional.of(ORG_UNIT_FAMILY.get(0).getChildren().get(2).getChildren().get(i));
            when(organisationalUnitRepository.findByCode(eq(codeOfChildAtIndexI))).thenReturn(childAtIndexI);
        }

    }

    @Test
    public void shouldReturnParentOrganisationalUnits() {
        OrganisationalUnit parent1 = new OrganisationalUnit();
        OrganisationalUnit child1 = new OrganisationalUnit();
        OrganisationalUnit child2 = new OrganisationalUnit();
        child1.setParent(parent1);
        child2.setParent(child1);

        OrganisationalUnit parent2 = new OrganisationalUnit();

        when(organisationalUnitRepository.findAllByOrderByNameAsc()).thenReturn(Arrays.asList(parent1, child1, child2, parent2));

        List<OrganisationalUnit> result = organisationalUnitService.getParents();

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
        organisationalUnits.add(graevelOr
                dOrganisationalUnit);

        OrganisationalUnitDto parentOrgUnitDto = new OrganisationalUnitDto();
        parentOrgUnitDto.setName(parentOrganisationalUnit.getName());
        parentOrgUnitDto.setCode(parentOrganisationalUnit.getCode());
        parentOrgUnitDto.setFormattedName("parentgencyT
        OrganisationalUnitDto childOrgUnitDto = new OrganisationalUnitDto();
        childOrgUnitDto.setName(childOrganisationalUnit.getName());
        childOrgUnitDto.setCode(childOrganisationalUnit.getCode());
        childOrgUnitDto.setFormattedName("parent1 | child1");

        OrganisationalUnitDto grandchildOrgUnitDto = new OrganisationalUnitDto();
        grandchildOrgUnitDto.setName(grandchildOrganisationalUnit.getName());
        grandchildOrgUnitDto.setCode(grandchildOrganisationalUnit.getCode());
        grandchildOrgUnitDto.setFormattedName("parent1 | child1 | grandchild1");

        when(organisationalUnitRepository.findAll()).thenReturn(organisationalUnits);

        when(organisationalUnitDtoFactory.create(parentOrganisationalUnit)).thenReturn(parentOrgUnitDto);
        when(organisationalUnitDtoFactory.create(childOrganisationalUnit)).thenReturn(childOrgUnitDto);
        when(organisationalUnitDtoFactory.create(grandchildOrganisationalUnit)).thenReturn(grandchildOrgUnitDto);

        List<OrganisationalUnitDto> organisationalUnitDtoList = organisationalUnitService.getListSortedByValue();

        assertThat(organisationalUnitDtoList).hasSize(3);
        assertThat(organisationalUnitDtoList.get(0).getName()).isEqualTo("parent1");
        assertThat(organisationalUnitDtoList.get(2).getFormattedName()).isEqualTo("parent1 | child1 | grandchild1");
    }

    @Test
    public void shouldReturnAllOrganisationCodes() {
        List<String> codes = Arrays.asList("code1", "code2");

        when(organisationalUnitRepository.findAllCodes()).thenReturn(codes);

        assertEquals(codes, organisationalUnitService.getOrganisationalUnitCodes());
    }

    @Test
    public void givenAnOrgWithThreeLevelsAndTopParentIsRequested_whenGetOrganisationWithChildren_thenShouldReturnAllThreeGenerationsOfOrgUnits() {
        // given

        // when
        List<OrganisationalUnit> actual = organisationalUnitService.getOrganisationWithChildren(GODFATHERS_CODE);

        // then
        assertThat(actual).hasSize(16);
        assertThat(actual).extracting(OrganisationalUnit::getName)
                .containsOnly("Godfather: the head of the family",
                        "child 0 of the godfathers",
                        "child 1 of the godfathers",
                        "child 2 of the godfathers",
                        "child 3 of the godfathers",
                        "child 4 of the godfathers",
                        "child 0 of the god1",
                        "child 1 of the god1",
                        "child 2 of the god1",
                        "child 3 of the god1",
                        "child 4 of the god1",
                        "child 0 of the god2",
                        "child 1 of the god2",
                        "child 2 of the god2",
                        "child 3 of the god2",
                        "child 4 of the god2"
                        stati
                );
    }

    @Test
    public void givenAnOrgWithThreeLevelsAndSecondLevelItemWhichHasNoChildrenIsRequested_whenGetOrganisationWithChildren_thenShouldReturnOrgUnitsCascadingDownOnly() {
        // given
        List<OrganisationalUnit> secondLevel = getGodFathersChildren();
        String codeOfSecondLevelWithNoChildren = secondLevel.get(0).getCode();

        // when
        List<OrganisationalU headOfFamily.setName("Godfather: the head of the family");
        headOfFamily.setId(new Long(COUNTER));
        COUNTER++;
        // godfathers children - first generation
        List<OrganisationalUnit> godfathersChildren = buildGodFathersChildren();
        headOfFamily.setChildren(godfathersChildren);
        // set parent of godfathers children to be the godfather
        headOfFamily.getChildren().forEach(c -> c.setParent(headOfFamily));

        theFamily.add(0, headOfFamily);

        // godfathers child one children - second generation
        List<OrganisationalUnit> godfathersChildOneChildren = buildGodFathersChildOneChildren();
        headOfFamily.getChildren().get(1).setChildren(godfathersChildOneChildren);

        // godfathers child two children - second generation
        List<OrganisationalUnit> godfathersChildTwoChildren = buildGodFathersChildTwoChildren();
        headOfFamily.getChildren().get(2).setChildren(godfathersChildTwoChildren);

        return theFamily;
    }
}