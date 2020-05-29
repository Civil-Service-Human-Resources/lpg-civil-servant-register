package uk.gov.cshr.civilservant.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.exception.CSRSApplicationException;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;

@Slf4j
@Service
public class CivilServantService {

    private CivilServantRepository civilServantRepository;

    public CivilServantService(CivilServantRepository civilServantRepository) {
        this.civilServantRepository = civilServantRepository;
    }

    public String getCivilServantUid() throws CSRSApplicationException {
       CivilServant cs = civilServantRepository.findByPrincipal()
               .orElseThrow(() -> new CSRSApplicationException("No Civil Servant found", new RuntimeException()));
       if(cs.getIdentity() != null && cs.getIdentity().getId() != null) {
           return cs.getIdentity().getUid();
       } else {
           throw new CSRSApplicationException("No Civil Servant found", new RuntimeException());
       }
   }

}
