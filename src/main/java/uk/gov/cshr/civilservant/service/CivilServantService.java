package uk.gov.cshr.civilservant.service;

import org.springframework.stereotype.Service;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;

import java.util.Optional;

@Service
public class CivilServantService {

    private final AgencyTokenService agencyTokenService;

    private final CivilServantRepository civilServantRepository;

    public CivilServantService(AgencyTokenService agencyTokenService, CivilServantRepository civilServantRepository) {
        this.agencyTokenService = agencyTokenService;
        this.civilServantRepository = civilServantRepository;
    }

    public void deleteCivilServant(CivilServant civilServant, String domain) {
        // check if there is an agency token and always delete
        Optional<OrganisationalUnit> orgUnitForCivilServant = civilServant.getOrganisationalUnit();
        if(orgUnitForCivilServant.isPresent()) {
            String code = orgUnitForCivilServant.get().getCode();
            Optional<AgencyToken> agencyToken = agencyTokenService.getAgencyTokenByDomainAndOrganisation(domain, code);
            if(agencyToken.isPresent()) {
                agencyTokenService.updateAgencyTokenSpacesAvailable(agencyToken.get(), true);
            }
        }
        civilServantRepository.delete(civilServant);
    }

}
