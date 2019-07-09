package uk.gov.cshr.civilservant.service;

import org.springframework.stereotype.Service;
import uk.gov.cshr.civilservant.repository.AgencyTokenRepository;

@Service
public class AgencyTokenService {
    private AgencyTokenRepository agencyTokenRepository;

    public AgencyTokenService(AgencyTokenRepository agencyTokenRepository) {
        this.agencyTokenRepository = agencyTokenRepository;
    }
}
