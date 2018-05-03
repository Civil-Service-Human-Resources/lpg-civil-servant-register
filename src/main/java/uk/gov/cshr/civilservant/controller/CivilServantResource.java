package uk.gov.cshr.civilservant.controller;

import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.Grade;
import uk.gov.cshr.civilservant.domain.Organisation;

import static com.google.common.base.Preconditions.checkArgument;

public class CivilServantResource {

    private String fullName;

    private Grade grade;

    private Organisation organisation;

    public CivilServantResource(CivilServant civilServant) {
        checkArgument(civilServant != null);
        this.fullName = civilServant.getFullName();
        this.grade = civilServant.getGrade();
        this.organisation = civilServant.getOrganisation();
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
}
