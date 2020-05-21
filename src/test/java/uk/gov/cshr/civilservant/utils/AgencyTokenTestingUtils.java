package uk.gov.cshr.civilservant.utils;

import uk.gov.cshr.civilservant.domain.AgencyDomain;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.dto.AgencyDomainDTO;
import uk.gov.cshr.civilservant.dto.AgencyTokenDto;
import uk.gov.cshr.civilservant.dto.AgencyTokenResponseDto;

import java.util.HashSet;
import java.util.Set;

public class AgencyTokenTestingUtils {

    private static final String UID = "UID";
    private static final AgencyTokenResponseDto AGENCY_TOKEN_RESPONSE_DTO = createAgencyTokenResponseDTO();
    private static final AgencyToken AGENCY_TOKEN = createAgencyToken();

    private AgencyTokenTestingUtils() {
    }

    public static AgencyTokenResponseDto getAgencyTokenResponseDto() {
        return AGENCY_TOKEN_RESPONSE_DTO;
    }

    public static AgencyToken getAgencyToken() {
        return AGENCY_TOKEN;
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

    public static String getExpectedFirstDomainNameFromSetOfAgencyDomains() {
        return "aDomain";
    }

    private static AgencyTokenResponseDto createAgencyTokenResponseDTO() {
        AgencyTokenResponseDto dto = new AgencyTokenResponseDto();
        dto.setToken("thisisatoken");
        dto.setCapacity(100);
        dto.setCapacityUsed(30);

        Set<AgencyDomainDTO> domains = new HashSet<AgencyDomainDTO>();
        AgencyDomainDTO domainDTO = new AgencyDomainDTO();
        domainDTO.setDomain("aDomain");
        domains.add(domainDTO);

        dto.setAgencyDomains(domains);
        return dto;
    }

    private static AgencyToken createAgencyToken(){
        AgencyToken at = new AgencyToken();
        at.setId(new Long(1));
        at.setToken("thisisatoken");
        at.setCapacity(100);
        at.setUid(UID);

        Set<AgencyDomain> domains = new HashSet<AgencyDomain>();
        AgencyDomain domain = new AgencyDomain();
        domain.setId(new Long(1));
        domain.setDomain("aDomain");
        domains.add(domain);

        at.setAgencyDomains(domains);
        return at;
    }
}
