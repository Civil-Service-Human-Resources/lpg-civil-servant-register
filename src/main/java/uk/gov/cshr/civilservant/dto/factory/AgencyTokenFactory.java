package uk.gov.cshr.civilservant.dto.factory;

import org.springframework.stereotype.Component;
import uk.gov.cshr.civilservant.domain.AgencyDomain;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.dto.AgencyTokenDTO;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AgencyTokenFactory {

    /* assumes any validation has already happened.*/
    public AgencyToken buildAgencyTokenFromAgencyTokenDTO(AgencyTokenDTO agencyTokenDTO, boolean isCreateNewToken) {
        AgencyToken agencytoken = new AgencyToken();

        if (isCreateNewToken) {
            agencytoken.setCapacityUsed(0);
        } else {
            agencytoken.setCapacityUsed(agencyTokenDTO.getCapacityUsed());
        }

        agencytoken.setToken(agencyTokenDTO.getToken());
        agencytoken.setCapacity(agencyTokenDTO.getCapacity());
        Set<AgencyDomain> agencyDomains = agencyTokenDTO.getAgencyDomains().stream().map(dtoDomain -> createAgencyDomain(dtoDomain.getDomain())).collect(Collectors.toSet());
        agencytoken.setAgencyDomains(agencyDomains);
        return agencytoken;
    }

    /* assumes any validation has already happened.*/
    private AgencyDomain createAgencyDomain(String domain) {
        AgencyDomain agencyDomain = new AgencyDomain();
        agencyDomain.setDomain(domain);
        return agencyDomain;
    }

}
