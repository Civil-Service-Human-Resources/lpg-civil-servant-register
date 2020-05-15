package uk.gov.cshr.civilservant.dto.factory;

import org.springframework.stereotype.Component;
import uk.gov.cshr.civilservant.domain.AgencyDomain;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.dto.AgencyTokenDTO;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AgencyTokenFactory {

    public AgencyToken buildAgencyTokenFromAgencyTokenDTO(AgencyTokenDTO agencyTokenDTO, boolean isCreateNewToken) {
        AgencyToken agencytoken = new AgencyToken();

        agencytoken.setToken(agencyTokenDTO.getToken());
        agencytoken.setCapacity(agencyTokenDTO.getCapacity());
        Set<AgencyDomain> agencyDomains = agencyTokenDTO.getAgencyDomains().stream().map(dtoDomain -> createAgencyDomain(dtoDomain.getDomain())).collect(Collectors.toSet());
        agencytoken.setAgencyDomains(agencyDomains);
        return agencytoken;
    }

    private AgencyDomain createAgencyDomain(String domain) {
        AgencyDomain agencyDomain = new AgencyDomain();
        agencyDomain.setDomain(domain);
        return agencyDomain;
    }
}
