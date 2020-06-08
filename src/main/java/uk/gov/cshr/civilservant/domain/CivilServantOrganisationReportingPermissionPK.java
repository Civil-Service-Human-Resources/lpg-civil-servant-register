package uk.gov.cshr.civilservant.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CivilServantOrganisationReportingPermissionPK implements Serializable {

    private Long civilServantId;

    private Long organisationId;
}