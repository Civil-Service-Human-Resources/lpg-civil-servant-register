package uk.gov.cshr.civilservant.utils;

import uk.gov.cshr.civilservant.domain.OrganisationalUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FamilyOrganisationUnits {

    private static int COUNTER = 0;

    private static String GODFATHERS_CODE = "gf";

    private List<OrganisationalUnit> family;

    public FamilyOrganisationUnits() {
        this.family = buildLargeFamilyOfOrganisationalUnits();
    }

    public List<OrganisationalUnit> getFamily() {
        return family;
    }

    public OrganisationalUnit getTopParent(){
        Optional<OrganisationalUnit> matchingObject = this.family.stream().
                filter(ou -> GODFATHERS_CODE.equals(ou.getCode())).findFirst();
        if(matchingObject.isPresent()) {
            return matchingObject.get();
        }
        return null;
    }

    public List<OrganisationalUnit> getParentsChildren(){
        return this.family.get(0).getChildren();
    }

    public List<OrganisationalUnit> getParentsChildrenChildren(int godFatherChildIndex){
        if(getParentsChildren() == null) {
            return new ArrayList<OrganisationalUnit>();
        }

        return getParentsChildren().get(godFatherChildIndex).getChildren();
    }

    private static List<OrganisationalUnit> buildLargeFamilyOfOrganisationalUnits() {
        // the family entirely
        List<OrganisationalUnit> theFamily = new ArrayList<>();

        OrganisationalUnit headOfFamily = new OrganisationalUnit();
        headOfFamily.setCode(GODFATHERS_CODE);
        headOfFamily.setParent(null);
        headOfFamily.setAbbreviation(GODFATHERS_CODE.toUpperCase());
        headOfFamily.setName("Godfather: the head of the family");
        headOfFamily.setId(new Long(COUNTER));
        COUNTER++;
        // godfathers children - first level
        List<OrganisationalUnit> godfathersChildren = buildGodFathersChildren();
        headOfFamily.setChildren(godfathersChildren);
        // set parent of godfathers children to be the godfather
        headOfFamily.getChildren().forEach(c -> c.setParent(headOfFamily));

        theFamily.add(0, headOfFamily);

        // godfathers child one children - second level
        List<OrganisationalUnit> godfathersChildOneChildren = buildGodFathersChildOneChildren();
        headOfFamily.getChildren().get(1).setChildren(godfathersChildOneChildren);
        // set the parent of godfathersChildOneChildren to be godfathersChildOne
        headOfFamily.getChildren().get(1).getChildren().forEach(c -> c.setParent(headOfFamily.getChildren().get(1)));

        // godfathers child two children - second level
        List<OrganisationalUnit> godfathersChildTwoChildren = buildGodFathersChildTwoChildren();
        headOfFamily.getChildren().get(2).setChildren(godfathersChildTwoChildren);
        // set the parent of godfathersChildTwoChildren to be godfathersChildTwo
        headOfFamily.getChildren().get(2).getChildren().forEach(c -> c.setParent(headOfFamily.getChildren().get(2)));

        return theFamily;
    }

    private static List<OrganisationalUnit> buildGodFathersChildren(){
        List<OrganisationalUnit> godfathersChildren = new ArrayList<>();
        for(int i=0; i<5; i++) {
            godfathersChildren.add(i, buildChild("god", i, "godfathers"));
        }
        return godfathersChildren;
    }

    private static List<OrganisationalUnit> buildGodFathersChildOneChildren(){
        List<OrganisationalUnit> godFatherChild1Children = new ArrayList<>();
        for(int i=0; i<5; i++) {
            godFatherChild1Children.add(i, buildChild("grandOne", i, "god1"));
        }
        return godFatherChild1Children;
    }

    private static List<OrganisationalUnit> buildGodFathersChildTwoChildren(){
        List<OrganisationalUnit> godFatherChild2Children = new ArrayList<>();
        for(int i=0; i<5; i++) {
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

}
