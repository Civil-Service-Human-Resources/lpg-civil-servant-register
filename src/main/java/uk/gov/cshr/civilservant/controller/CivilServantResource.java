package uk.gov.cshr.civilservant.controller;

import uk.gov.cshr.civilservant.domain.*;

import static com.google.common.base.Preconditions.checkArgument;

public class CivilServantResource {

    private String fullName;

    private Grade grade;

    private Organisation organisation;

    private Profession profession;

    private JobRole jobRole;

    public CivilServantResource(CivilServant civilServant) {
        checkArgument(civilServant != null);
        this.fullName = civilServant.getFullName();
        this.grade = civilServant.getGrade();
        this.organisation = civilServant.getOrganisation();
        this.profession = civilServant.getProfession();
        this.jobRole = civilServant.getJobRole();
    }

    public String getFullName() {
        return fullName;
    }

    public Grade getGrade() {
        return grade;
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
}
