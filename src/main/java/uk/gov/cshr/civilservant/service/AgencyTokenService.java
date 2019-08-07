package uk.gov.cshr.civilservant.service;

import org.springframework.stereotype.Service;
import uk.gov.cshr.civilservant.domain.AgencyToken;
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
        return agencyTokenRepository.findByDomainTokenAndCode(domain, token, code);
    }
}
