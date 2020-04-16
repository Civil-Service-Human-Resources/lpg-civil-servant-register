package uk.gov.cshr.civilservant.domain;

import java.io.Serializable;
import java.util.Objects;

public class CivilServantOrganisationReportingPermissionPK implements Serializable {

    private Long civilServantId;

    private Long organisationId;

    public CivilServantOrganisationReportingPermissionPK() {
    }

    public CivilServantOrganisationReportingPermissionPK(Long civilServantId, Long organisationId) {
        this.civilServantId = civilServantId;
        this.organisationId = organisationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CivilServantOrganisationReportingPermissionPK that = (CivilServantOrganisationReportingPermissionPK) o;
        return Objects.equals(civilServantId, that.civilServantId) &&
                Objects.equals(organisationId, that.organisationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(civilServantId, organisationId);
    }
}
