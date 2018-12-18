package uk.gov.cshr.civilservant.resource;

import uk.gov.cshr.civilservant.domain.*;

import java.util.Set;

public class CivilServantResource {

    private String fullName;

    private Grade grade;

    private OrganisationalUnit organisationalUnit;

    private Profession profession;

    private Set<Profession> otherAreasOfWork;

    private Set<Interest> interests;

    private String lineManagerName;

    private String lineManagerEmailAddress;

    public CivilServantResource(CivilServant civilServant, String lineManagerEmailAddress) {
        this.fullName = civilServant.getFullName();
        this.grade = civilServant.getGrade();
        this.organisationalUnit = civilServant.getOrganisationalUnit();
        this.profession = civilServant.getProfession();
        this.interests = civilServant.getInterests();
        this.otherAreasOfWork = civilServant.getOtherAreasOfWork();
        this.lineManagerName = civilServant.getLineManagerName();
        this.lineManagerEmailAddress = lineManagerEmailAddress;
    }

    public String getFullName() {
        return fullName;
    }

    public Grade getGrade() {
        return grade;
    }

    public String getLineManagerEmailAddress() {
        return lineManagerEmailAddress;
    }

    public String getLineManagerName() {
        return lineManagerName;
    }

    public OrganisationalUnit getOrganisationalUnit() {
        return organisationalUnit;
    }

    public Profession getProfession() {
        return profession;
    }

    public Set<Profession> getOtherAreasOfWork() {
        return otherAreasOfWork;
    }

    public Set<Interest> getInterests() {
        return interests;
    }
}
