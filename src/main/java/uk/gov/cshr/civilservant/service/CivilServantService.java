package uk.gov.cshr.civilservant.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.exception.CivilServantNotFoundException;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;

@Slf4j
@Service
public class CivilServantService {

    private CivilServantRepository civilServantRepository;

    public CivilServantService(CivilServantRepository civilServantRepository) {
        this.civilServantRepository = civilServantRepository;
    }

    public String getCivilServantUid() {
        CivilServant cs = civilServantRepository.findByPrincipal()
                .orElseThrow(() -> new CivilServantNotFoundException());
        if (cs.getIdentity() != null && cs.getIdentity().getUid() != null) {
            return cs.getIdentity().getUid();
        }
        throw new CivilServantNotFoundException();
    }

}
