package uk.gov.cshr.civilservant.dto.factory;

import uk.gov.cshr.civilservant.domain.AgencyDomain;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.dto.AgencyTokenDTO;

import java.util.Set;
import java.util.stream.Collectors;

public class AgencyTokenFactory {

    private AgencyTokenFactory() {
    }

    public static AgencyToken buildAgencyTokenFromAgencyTokenDTO(AgencyTokenDTO agencyTokenDTO) {
        AgencyToken agencytoken = new AgencyToken();
        // add here as should be validated by now.
        agencytoken.setCapacityUsed(agencyTokenDTO.getCapacityUsed());
        agencytoken.setToken(agencyTokenDTO.getToken());
        agencytoken.setCapacity(agencyTokenDTO.getCapacity());
        Set<AgencyDomain> agencyDomains = agencyTokenDTO.getAgencyDomains().stream().map(dtoDomain -> createAgencyDomain(dtoDomain.getDomain())).collect(Collectors.toSet());
        agencytoken.setAgencyDomains(agencyDomains);
        return agencytoken;
    }

    private static AgencyDomain createAgencyDomain(String domain) {
        AgencyDomain agencyDomain = new AgencyDomain();
        agencyDomain.setDomain(domain);
        return agencyDomain;
    }
}
