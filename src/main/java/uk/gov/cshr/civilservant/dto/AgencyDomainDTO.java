package uk.gov.cshr.civilservant.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AgencyDomainDTO {

    private String domain;

    public AgencyDomainDTO(String domain) {
        this.domain = domain;
    }
}
