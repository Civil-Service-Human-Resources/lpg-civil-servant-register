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

        if (civilServant.getGrade().isPresent()) {
            this.grade = civilServant.getGrade().get();
        }

        if (civilServant.getOrganisationalUnit().isPresent()) {
            this.organisationalUnit = civilServant.getOrganisationalUnit().get();
        }

        if (civilServant.getProfession().isPresent()) {
            this.profession = civilServant.getProfession().get();
        }

        this.interests = civilServant.getInterests();
        this.otherAreasOfWork = civilServant.getOtherAreasOfWork();
        this.lineManagerName = civilServant.getLineManagerName();
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
