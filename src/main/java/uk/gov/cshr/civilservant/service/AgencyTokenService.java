package uk.gov.cshr.civilservant.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.exception.NotEnoughSpaceAvailableException;
import uk.gov.cshr.civilservant.exception.TokenDoesNotExistException;
import uk.gov.cshr.civilservant.repository.AgencyTokenRepository;
import uk.gov.cshr.civilservant.repository.OrganisationalUnitRepository;

@Service
public class AgencyTokenService {

    private AgencyTokenRepository agencyTokenRepository;
    private OrganisationalUnitRepository organisationalUnitRepository;

    public AgencyTokenService(AgencyTokenRepository agencyTokenRepository, OrganisationalUnitRepository organisationalUnitRepository) {
        this.agencyTokenRepository = agencyTokenRepository;
        this.organisationalUnitRepository = organisationalUnitRepository;
    }

    public Iterable<AgencyToken> getAllAgencyTokens() {
        return agencyTokenRepository.findAll();
    }

    public Iterable<AgencyToken> getAllAgencyTokensByDomain(String domain) {
        return agencyTokenRepository.findAllByDomain(domain);
    }

    public Optional<AgencyToken> getAgencyTokenByDomainAndToken(String domain, String token) {
        return agencyTokenRepository.findByDomainAndToken(domain, token);
    }

    public Optional<AgencyToken> getAgencyTokenByDomainTokenAndOrganisation(String domain, String token, String code) {

        Optional<AgencyToken> agencyToken = agencyTokenRepository.findByDomainAndToken(domain, token);

        if (agencyToken.isPresent()) {
            Optional<OrganisationalUnit> tokenOwner = organisationalUnitRepository.findOrganisationByAgencyToken(agencyToken.get());

            if (tokenOwner.isPresent()) {
                if (organisationContainsCode(tokenOwner.get(), code)) {
                    return agencyToken;
                }
            }
        }

        return Optional.empty();
    }

    private boolean organisationContainsCode(OrganisationalUnit organisationalUnit, String code) {
        if (organisationalUnit.getCode().equals(code)) {
            return true;
        } else if (organisationalUnit.hasChildren()) {
            for (OrganisationalUnit childUnit : organisationalUnit.getChildren()) {
                return organisationContainsCode(childUnit, code);
            }
        } else {
            return false;
        }
        return false;
    }

    public Optional<AgencyToken> getAgencyTokenByDomainAndOrganisation(String domain, String code) {
        return agencyTokenRepository.findByDomainAndCode(domain, code);
    }

    @Transactional
    public Optional<AgencyToken> updateAgencyTokenSpacesAvailable(String domain, String token, List<String> codes, boolean isRemoveUser) {
        for (String code: codes)
        {
            Optional<AgencyToken> agencyToken = agencyTokenRepository.findByDomainTokenAndCodeIncludingAgencyDomains(domain, token, code);
            if (agencyToken.isPresent()) {
                // if it exists - do update
                updateSpacesAvailable(agencyToken.get(), isRemoveUser);
                return agencyToken;
            }
        }

        throw new TokenDoesNotExistException(domain);
    }

    public void deleteAgencyToken(AgencyToken agencyToken) {
        agencyTokenRepository.delete(agencyToken);
    }

    private AgencyToken updateSpacesAvailable(AgencyToken agencyToken, boolean isRemoveUser) {
        if (isRemoveUser) {
            return removeUserFromAgencyTokenUpdateSpacesAvailable(agencyToken);
        } else {
            return addUserToAgencyTokenUpdateSpacesAvailable(agencyToken);
        }

    }

    private AgencyToken removeUserFromAgencyTokenUpdateSpacesAvailable(AgencyToken agencyToken) {
        // check capacity used doesn't go less than zero
        // unlikely scenario but could happen theoretically
        if((agencyToken.getCapacityUsed() - 1) < 0){
            throw new NotEnoughSpaceAvailableException(agencyToken.getToken());
        }

        // update
        int newCapacityUsed = agencyToken.getCapacityUsed() - 1;
        agencyToken.setCapacityUsed(newCapacityUsed);
        return agencyTokenRepository.save(agencyToken);
    }

    private AgencyToken addUserToAgencyTokenUpdateSpacesAvailable(AgencyToken agencyToken) {
        // check existing quota
        int existing = agencyToken.getCapacityUsed();

        // check if enough
        if (existing + 1 > agencyToken.getCapacity()) {
            throw new NotEnoughSpaceAvailableException(agencyToken.getToken());
        }

        // update
        int newCapacityUsed = agencyToken.getCapacityUsed() + 1;
        agencyToken.setCapacityUsed(newCapacityUsed);
        return agencyTokenRepository.save(agencyToken);
    }

}
