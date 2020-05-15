package uk.gov.cshr.civilservant.service;

import org.springframework.stereotype.Service;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.repository.AgencyTokenRepository;
import uk.gov.cshr.civilservant.repository.OrganisationalUnitRepository;

import java.util.Optional;

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

    public void deleteAgencyToken(AgencyToken agencyToken) {
        agencyTokenRepository.delete(agencyToken);
    }
}
