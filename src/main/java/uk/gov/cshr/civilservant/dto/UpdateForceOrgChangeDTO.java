package uk.gov.cshr.civilservant.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateForceOrgChangeDTO {
    @NotNull
    private boolean forceOrgChange;
}
