package uk.gov.cshr.civilservant.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@IdClass(CivilServantOrganisationReportingPermissionPK.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CivilServantOrganisationReportingPermission {

    @Id
    private Long civilServantId;

    @Id
    private Long organisationId;
}