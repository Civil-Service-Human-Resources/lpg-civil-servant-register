package uk.gov.cshr.civilservant.utils;

import uk.gov.cshr.civilservant.domain.AgencyDomain;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.dto.AgencyDomainDTO;
import uk.gov.cshr.civilservant.dto.AgencyTokenDto;

import java.util.HashSet;
import java.util.Set;

public class AgencyTokenTestingUtils {

    private static final String UID = "UID";

    private AgencyTokenTestingUtils() {
    }

    public static AgencyToken createAgencyToken(int i){
        AgencyToken at = new AgencyToken();
        at.setId(new Long(i));
        at.setToken("thisisatoken"+i);
        at.setCapacity(100);
        at.setUid(UID);

        Set<AgencyDomain> domains = new HashSet<AgencyDomain>();
        AgencyDomain domain = new AgencyDomain();
        domain.setId(new Long(i));
        domain.setDomain("aDomain"+i);
        domains.add(domain);

        at.setAgencyDomains(domains);
        return at;
    }

    public static AgencyTokenDto createAgencyTokenDTO() {
        AgencyTokenDto dto = new AgencyTokenDto();
        dto.setToken("thisisatoken");
        dto.setCapacity(100);
        dto.setUid(UID);

        Set<AgencyDomainDTO> domains = new HashSet<AgencyDomainDTO>();
        AgencyDomainDTO domainDTO = new AgencyDomainDTO();
        domainDTO.setDomain("aDomain");
        domains.add(domainDTO);

        dto.setAgencyDomains(domains);
        return dto;
    }
}
