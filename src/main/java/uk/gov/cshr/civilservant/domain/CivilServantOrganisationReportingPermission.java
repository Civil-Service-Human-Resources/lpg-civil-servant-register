package uk.gov.cshr.civilservant.domain;

import javax.persistence.*;

@Entity
@IdClass(CivilServantOrganisationReportingPermissionPK.class)
public class CivilServantOrganisationReportingPermission {

    @Id
    private Long civilServantId;

    @Id
    private Long organisationId;

    public CivilServantOrganisationReportingPermission() {
    }

    public CivilServantOrganisationReportingPermission(Long civilServantId, Long organisationId) {
        this.civilServantId = civilServantId;
        this.organisationId = organisationId;
    }
}
