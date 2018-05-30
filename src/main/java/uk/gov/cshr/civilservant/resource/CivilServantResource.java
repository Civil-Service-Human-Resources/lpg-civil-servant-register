package uk.gov.cshr.civilservant.resource;

import uk.gov.cshr.civilservant.domain.*;

import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

public class CivilServantResource {

    private String fullName;

    private Grade grade;

    private Organisation organisation;

    private Profession profession;

    private JobRole jobRole;

    private Set<Profession> otherAreasOfWork;

    private String lineManagerUid;
    private String lineManagerEmail;

    public CivilServantResource(CivilServant civilServant) {
        checkArgument(civilServant != null);
        this.fullName = civilServant.getFullName();
        this.grade = civilServant.getGrade();
        this.organisation = civilServant.getOrganisation();
        this.profession = civilServant.getProfession();
        this.jobRole = civilServant.getJobRole();
        this.otherAreasOfWork = civilServant.getOtherAreasOfWork();
        this.lineManagerUid = civilServant.getLineManagerUid();
        this.lineManagerEmail = civilServant.getLineManagerEmail();
    }

    public String getFullName() {
        return fullName;
    }

    public Grade getGrade() {
        return grade;
    }


    public String getLineManagerUid() {
        return lineManagerUid;
    }


    public String getLineManagerEmail() {
        return lineManagerEmail;
    }


    public Organisation getOrganisation() {
        return organisation;
    }

    public Profession getProfession() {
        return profession;
    }

    public JobRole getJobRole() {
        return jobRole;
    }


    public Set<Profession> getOtherAreasOfWork() {
        return otherAreasOfWork;
    }
}
