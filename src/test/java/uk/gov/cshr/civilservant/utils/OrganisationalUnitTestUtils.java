package uk.gov.cshr.civilservant.utils;

import uk.gov.cshr.civilservant.domain.OrganisationalUnit;

public class OrganisationalUnitTestUtils {

    public static OrganisationalUnit buildOrgUnit(String code, int index, String name) {
        OrganisationalUnit org = new OrganisationalUnit();
        org.setCode(code + index);
        org.setAbbreviation(code.toUpperCase() + index);
        org.setName(name + " " + index);
        org.setId(new Long(index));
        return org;
    }
}
