package uk.gov.cshr.civilservant.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.exception.NotEnoughSpaceAvailableException;
import uk.gov.cshr.civilservant.exception.TokenDoesNotExistException;
import uk.gov.cshr.civilservant.repository.AgencyTokenRepository;

import java.util.List;
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

    public Optional<AgencyToken> getAgencyTokenByDomainAndOrganisation(String domain, String code) {
        return agencyTokenRepository.findByDomainAndCode(domain, code);
    }

    @Transactional
    public Optional<AgencyToken> updateAgencyTokenSpacesAvailable(String domain, String token, List<String> codes, boolean isRemoveUser) {
        for (String code : codes) {
            Optional<AgencyToken> agencyToken = agencyTokenRepository.findByDomainTokenAndCodeIncludingAgencyDomains(domain, token, code);
            if (agencyToken.isPresent()) {
                // if it exists - do update
                updateSpacesAvailable(agencyToken.get(), isRemoveUser);
                return agencyToken;
            }
        }
        throw new TokenDoesNotExistException(domain);

    }

    @Transactional
    public AgencyToken updateAgencyTokenSpacesAvailable(AgencyToken agencyToken, boolean isRemoveUser) {
        return updateSpacesAvailable(agencyToken, isRemoveUser);
    }


            public Optional<AgencyToken> updateAgencyTokenSpacesAvailable(String domain, String token, String code, boolean isRemoveUser) {
            // find token
            Optional<AgencyToken> agencyToken = agencyTokenRepository.findByDomainTokenAndCodeIncludingAgencyDomains(domain, token, code);

            if (agencyToken.isPresent()) {
                // if it exists - do update
                return Optional.of(updateSpacesAvailable(agencyToken.get(), isRemoveUser));
            } else {
                // Not found
                throw new TokenDoesNotExistException(domain);
//>>>>>>> 250dd26... LC-14 - Starting to add delete identity code.  work in progress.
            }
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
