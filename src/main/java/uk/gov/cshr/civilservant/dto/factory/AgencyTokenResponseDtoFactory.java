package uk.gov.cshr.civilservant.dto.factory;

import org.springframework.stereotype.Component;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.dto.AgencyDomainDTO;
import uk.gov.cshr.civilservant.dto.AgencyTokenResponseDto;

import java.util.stream.Collectors;

@Component
public class AgencyTokenResponseDtoFactory {

    public AgencyTokenResponseDto buildDto(AgencyToken agencyToken, int capacityUsed) {
        AgencyTokenResponseDto dto = new AgencyTokenResponseDto();
        dto.setAgencyDomains(agencyToken.getAgencyDomains()
                .stream()
                .map(ad -> new AgencyDomainDTO(ad.getDomain()))
                .collect(Collectors.toSet()));
        dto.setCapacity(agencyToken.getCapacity());
        dto.setToken(agencyToken.getToken());
        dto.setCapacityUsed(capacityUsed);
        return dto;
    }
}
