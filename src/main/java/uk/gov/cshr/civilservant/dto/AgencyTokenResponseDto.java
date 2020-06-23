package uk.gov.cshr.civilservant.dto;

import lombok.Data;

import java.util.Set;

@Data
public class AgencyTokenResponseDto {

    private String token;

    private int capacity;

    private int capacityUsed;

    private Set<AgencyDomainDTO> agencyDomains;

}
