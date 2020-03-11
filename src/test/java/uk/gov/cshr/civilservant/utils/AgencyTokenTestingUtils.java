package uk.gov.cshr.civilservant.utils;

import uk.gov.cshr.civilservant.domain.AgencyDomain;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.dto.AgencyDomainDTO;
import uk.gov.cshr.civilservant.dto.AgencyTokenDTO;

import java.util.HashSet;
import java.util.Set;

public class AgencyTokenTestingUtils {

    private AgencyTokenTestingUtils() {
    }

    public static AgencyToken createAgencyToken(){
        AgencyToken at = new AgencyToken();
        at.setToken("thisisatoken");
        at.setCapacity(100);
        at.setCapacityUsed(0);

        Set<AgencyDomain> domains = new HashSet<AgencyDomain>();
        AgencyDomain domain = new AgencyDomain();
        domain.setDomain("aDomain");
        domains.add(domain);

        at.setAgencyDomains(domains);
        return at;
    }

    public static AgencyTokenDTO createAgencyTokenDTO(){
        AgencyTokenDTO dto = new AgencyTokenDTO();
        dto.setToken("thisisatoken");
        dto.setCapacity(100);
        dto.setCapacityUsed(0);

        Set<AgencyDomainDTO> domains = new HashSet<AgencyDomainDTO>();
        AgencyDomainDTO domainDTO = new AgencyDomainDTO();
        domainDTO.setDomain("aDomain");
        domains.add(domainDTO);

        dto.setAgencyDomains(domains);
        return dto;
    }
}
