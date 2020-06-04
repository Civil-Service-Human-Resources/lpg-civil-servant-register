package uk.gov.cshr.civilservant.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IdentityAgencyResponseDTO {

    private String uid;
    private String agencyTokenUid;
}
