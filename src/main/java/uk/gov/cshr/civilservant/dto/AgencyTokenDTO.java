package uk.gov.cshr.civilservant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AgencyTokenDTO {

    private String organisation;
    private String domain;
    private String agencyTokenCode;
}
