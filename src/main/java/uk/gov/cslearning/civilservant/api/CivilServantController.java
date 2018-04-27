package uk.gov.cslearning.civilservant.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.civilservant.domain.CivilServant;
import uk.gov.cslearning.civilservant.domain.Identity;
import uk.gov.cslearning.civilservant.repository.CivilServantRepository;
import uk.gov.cslearning.civilservant.repository.IdentityRepository;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

@RestController
@RequestMapping("/civil-servant")
public class CivilServantController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CivilServantController.class);

    private CivilServantRepository civilServantRepository;

    private IdentityRepository identityRepository;

    @Autowired
    public CivilServantController(CivilServantRepository civilServantRepository, IdentityRepository identityRepository) {
        checkArgument(civilServantRepository != null);
        checkArgument(identityRepository != null);
        this.civilServantRepository = civilServantRepository;
        this.identityRepository = identityRepository;
    }

    @GetMapping
    @Transactional
    public ResponseEntity<CivilServant> get(Principal principal) {
        LOGGER.debug("Getting civil servant details");

        String identityUid = principal.getName();

        Optional<Identity> identity = identityRepository.findByUid(identityUid);

        Identity storedIdentity = identity.orElseGet(() -> {
            LOGGER.debug("No identity exists for uid {}, creating.", identityUid);
            Identity newIdentity = new Identity(identityUid);
            return identityRepository.save(newIdentity);
        });

        Optional<CivilServant> civilServant = civilServantRepository.findByIdentity(storedIdentity);
        CivilServant storedCivilServant = civilServant.orElseGet(() -> {
            LOGGER.debug("No civil servant exists for identity {}, creating.", identity);
            CivilServant newCivilServant = new CivilServant(storedIdentity);
            return civilServantRepository.save(newCivilServant);
        });

        return ResponseEntity.ok(storedCivilServant);
    }
}
