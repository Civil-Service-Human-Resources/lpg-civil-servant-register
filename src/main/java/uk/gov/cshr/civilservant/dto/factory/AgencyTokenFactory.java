package uk.gov.cshr.civilservant.dto.factory;

import org.springframework.stereotype.Component;
import uk.gov.cshr.civilservant.domain.AgencyDomain;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.dto.AgencyTokenDTO;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AgencyTokenFactory {

    public AgencyToken buildAgencyTokenFromAgencyTokenDto(AgencyTokenDTO agencyTokenDto) {
        AgencyToken agencytoken = new AgencyToken();

        String uid = (agencyTokenDto.getUid() != null) ? agencyTokenDto.getUid() : UUID.randomUUID().toString();

        agencytoken.setUid(uid);
        agencytoken.setToken(agencyTokenDto.getToken());
        agencytoken.setCapacity(agencyTokenDto.getCapacity());

        Set<AgencyDomain> agencyDomains = agencyTokenDto.getAgencyDomains()
                .stream()
                .map(dtoDomain -> createAgencyDomain(dtoDomain.getDomain()))
                .collect(Collectors.toSet());

        agencytoken.setAgencyDomains(agencyDomains);
        return agencytoken;
    }

    private AgencyDomain createAgencyDomain(String domain) {
        AgencyDomain agencyDomain = new AgencyDomain();
        agencyDomain.setDomain(domain);
        return agencyDomain;
    }
}
