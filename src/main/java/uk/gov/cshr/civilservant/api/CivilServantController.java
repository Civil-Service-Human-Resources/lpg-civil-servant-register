package uk.gov.cshr.civilservant.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.Identity;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;
import uk.gov.cshr.civilservant.repository.IdentityRepository;

import javax.transaction.Transactional;
import javax.validation.Valid;
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
    public ResponseEntity<CivilServantResource> get(Principal principal) {
        LOGGER.debug("Getting civil servant details for user {}", principal.getName());
        return ResponseEntity.ok(new CivilServantResource(findOrCreateCivilServant(principal)));
    }

    @PutMapping
    @Transactional
    public ResponseEntity<Void> update(@Valid @RequestBody CivilServantResource civilServantResource, BindingResult bindingResult, Principal principal) {
        LOGGER.debug("Updating civil servant details for user {}", principal.getName());

        if (bindingResult.hasErrors()) {
            LOGGER.debug("Request has errors, responding with bad request", bindingResult);
            return ResponseEntity.badRequest().build();
        }

        CivilServant civilServant = findOrCreateCivilServant(principal);
        civilServant.setFullName(civilServantResource.getFullName());

        civilServantRepository.save(civilServant);

        return ResponseEntity.noContent().build();
    }

    private CivilServant findOrCreateCivilServant(Principal principal) {

        String identityUid = principal.getName();

        Optional<Identity> identity = identityRepository.findByUid(identityUid);

        Identity storedIdentity = identity.orElseGet(() -> {
            LOGGER.debug("No identity exists for uid {}, creating.", identityUid);
            Identity newIdentity = new Identity(identityUid);
            return identityRepository.save(newIdentity);
        });

        Optional<CivilServant> civilServant = civilServantRepository.findByIdentity(storedIdentity);

        return civilServant.orElseGet(() -> {
            LOGGER.debug("No civil servant exists for identity {}, creating.", identity);
            CivilServant newCivilServant = new CivilServant(storedIdentity);
            return civilServantRepository.save(newCivilServant);
        });
    }
}
