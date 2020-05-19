package uk.gov.cshr.civilservant.dto.factory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cshr.civilservant.domain.AgencyDomain;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.dto.AgencyDomainDTO;
import uk.gov.cshr.civilservant.dto.AgencyTokenResponseDto;
import uk.gov.cshr.civilservant.utils.AgencyTokenTestingUtils;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class AgencyTokenResponseDtoFactoryTest {

    private static final int EXPECTED_CAPACITY_USED = 50;

    @InjectMocks
    private AgencyTokenResponseDtoFactory agencyTokenResponseDtoFactory;

    @Test
    public void buildAgencyTokenResponseDtoShouldIncludeAllExpectedFields() {
        AgencyToken agencyToken = createAgencyToken();

        AgencyTokenResponseDto actual = agencyTokenResponseDtoFactory.buildDto(agencyToken, EXPECTED_CAPACITY_USED);

        assertEquals(actual.getCapacity(), agencyToken.getCapacity());
        assertEquals(actual.getCapacityUsed(), EXPECTED_CAPACITY_USED);
        assertEquals(actual.getToken(), agencyToken.getToken());
        Set<AgencyDomainDTO> actualAgencyDomains = actual.getAgencyDomains();
        assertEquals(actualAgencyDomains.size(), 1);
        AgencyDomainDTO[] actualAgencyDomainsAsAnArray = actualAgencyDomains.toArray(new AgencyDomainDTO[actualAgencyDomains.size()]);
        assertEquals(actualAgencyDomainsAsAnArray[0].getDomain(), AgencyTokenTestingUtils.getExpectedFirstDomainNameFromSetOfAgencyDomains());
    }

    @Test
    public void buildAgencyTokenResponseDtoWithEmptySetShouldIncludeAllExpectedFields() {
        AgencyToken agencyTokenWithNoDomains = createAgencyToken();
        agencyTokenWithNoDomains.getAgencyDomains().clear();

        AgencyTokenResponseDto actual = agencyTokenResponseDtoFactory.buildDto(agencyTokenWithNoDomains, EXPECTED_CAPACITY_USED);

        assertEquals(actual.getCapacity(), agencyTokenWithNoDomains.getCapacity());
        assertEquals(actual.getCapacityUsed(), EXPECTED_CAPACITY_USED);
        assertEquals(actual.getToken(), agencyTokenWithNoDomains.getToken());
        Set<AgencyDomainDTO> actualAgencyDomains = actual.getAgencyDomains();
        assertEquals(actualAgencyDomains.size(), 0);
    }

    private static AgencyToken createAgencyToken(){
        AgencyToken at = new AgencyToken();
        at.setId(new Long(1));
        at.setToken("thisisatoken");
        at.setCapacity(100);
       // at.setUid(UID);

        Set<AgencyDomain> domains = new HashSet<AgencyDomain>();
        AgencyDomain domain = new AgencyDomain();
        domain.setId(new Long(1));
        domain.setDomain("aDomain");
        domains.add(domain);

        at.setAgencyDomains(domains);
        return at;
    }

}
