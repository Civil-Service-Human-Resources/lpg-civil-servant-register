package uk.gov.cshr.civilservant.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.Grade;
import uk.gov.cshr.civilservant.domain.Identity;
import uk.gov.cshr.civilservant.domain.Organisation;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;
import uk.gov.cshr.civilservant.repository.GradeRepository;
import uk.gov.cshr.civilservant.repository.IdentityRepository;
import uk.gov.cshr.civilservant.repository.OrganisationRepository;

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

    private GradeRepository gradeRepository;

    private OrganisationRepository organisationRepository;

    @Autowired
    public CivilServantController(CivilServantRepository civilServantRepository, IdentityRepository identityRepository,
                                  GradeRepository gradeRepository, OrganisationRepository organisationRepository) {
        checkArgument(civilServantRepository != null);
        checkArgument(identityRepository != null);
        checkArgument(gradeRepository != null);
        checkArgument(organisationRepository != null);
        this.civilServantRepository = civilServantRepository;
        this.identityRepository = identityRepository;
        this.gradeRepository = gradeRepository;
        this.organisationRepository = organisationRepository;
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

        if (civilServantResource.getGradeId() != null) {
            Long gradeId = civilServantResource.getGradeId();
            LOGGER.debug("Looking up grade with id {}", gradeId);
            Optional<Grade> grade = gradeRepository.findById(gradeId);
            if (grade.isPresent()) {
                civilServant.setGrade(grade.get());
            } else {
                return ResponseEntity.badRequest().build();
            }
        }

        if (civilServantResource.getOrganisationId() != null) {
            Long organisationId = civilServantResource.getOrganisationId();
            LOGGER.debug("Looking up organisation with id {}", organisationId);
            Optional<Organisation> organisation = organisationRepository.findById(organisationId);
            if (organisation.isPresent()) {
                civilServant.setOrganisation(organisation.get());
            } else {
                return ResponseEntity.badRequest().build();
            }
        }

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
