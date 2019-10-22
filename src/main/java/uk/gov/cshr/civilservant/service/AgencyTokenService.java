package uk.gov.cshr.civilservant.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.exception.NotEnoughSpaceAvailableException;
import uk.gov.cshr.civilservant.exception.TokenDoesNotExistException;
import uk.gov.cshr.civilservant.repository.AgencyTokenRepository;

import java.util.Optional;

@Service
public class AgencyTokenService {
    private AgencyTokenRepository agencyTokenRepository;

    public AgencyTokenService(AgencyTokenRepository agencyTokenRepository) {
        this.agencyTokenRepository = agencyTokenRepository;
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
        return agencyTokenRepository.findByDomainTokenAndCodeIncludingAgencyDomains(domain, token, code);
    }

    @Transactional
    public Optional<AgencyToken> updateAgencyTokenSpacesAvailable(String domain, String token, String code, boolean isRemoveUser) {
        // find token
        Optional<AgencyToken> agencyToken = agencyTokenRepository.findByDomainTokenAndCodeIncludingAgencyDomains(domain, token, code);

        if (agencyToken.isPresent()) {
            // if it exists - do update
            updateSpacesAvailable(agencyToken.get(), isRemoveUser);
        } else {
            // Not found
            throw new TokenDoesNotExistException(domain);
        }

        return agencyToken;
    }

    public void deleteAgencyToken(AgencyToken agencyToken) {
        agencyTokenRepository.delete(agencyToken);
    }

    private void updateSpacesAvailable(AgencyToken agencyToken, boolean isRemoveUser) {
        if (isRemoveUser) {
            removeUserFromAgencyTokenUpdateSpacesAvailable(agencyToken);
        } else {
            addUserToAgencyTokenUpdateSpacesAvailable(agencyToken);
        }

    }

    private void removeUserFromAgencyTokenUpdateSpacesAvailable(AgencyToken agencyToken) {
        // update
        int newCapacityUsed = agencyToken.getCapacityUsed() - 1;
        agencyToken.setCapacityUsed(newCapacityUsed);
        agencyTokenRepository.save(agencyToken);
    }

    private void addUserToAgencyTokenUpdateSpacesAvailable(AgencyToken agencyToken) {
        // check existing quota
        int existing = agencyToken.getCapacityUsed();

        // check if enough
        if (existing + 1 > agencyToken.getCapacity()) {
            throw new NotEnoughSpaceAvailableException(agencyToken.getToken());
        }

        // update
        int newCapacityUsed = agencyToken.getCapacityUsed() + 1;
        agencyToken.setCapacityUsed(newCapacityUsed);
        agencyTokenRepository.save(agencyToken);
    }

}
