package uk.gov.cshr.civilservant.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.dto.OrgCodeDTO;
import uk.gov.cshr.civilservant.dto.UpdateForceOrgChangeDTO;
import uk.gov.cshr.civilservant.dto.UpdateOrganisationDTO;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;
import uk.gov.cshr.civilservant.repository.OrganisationalUnitRepository;
import uk.gov.cshr.civilservant.resource.CivilServantResource;
import uk.gov.cshr.civilservant.resource.factory.CivilServantResourceFactory;
import uk.gov.cshr.civilservant.service.LineManagerService;
import uk.gov.cshr.civilservant.service.identity.IdentityFromService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@RepositoryRestController
@RequestMapping("/civilServants")
@RestResource(exported = false)
public class CivilServantController implements ResourceProcessor<RepositoryLinksResource> {

    private final LineManagerService lineManagerService;

    private final CivilServantRepository civilServantRepository;

    private final RepositoryEntityLinks repositoryEntityLinks;

    private final CivilServantResourceFactory civilServantResourceFactory;

    private final OrganisationalUnitRepository organisationalUnitRepository;

    public CivilServantController(LineManagerService lineManagerService, CivilServantRepository civilServantRepository,
                                  RepositoryEntityLinks repositoryEntityLinks,
                                  CivilServantResourceFactory civilServantResourceFactory,
                                  OrganisationalUnitRepository organisationalUnitRepository) {
        this.lineManagerService = lineManagerService;
        this.civilServantRepository = civilServantRepository;
        this.repositoryEntityLinks = repositoryEntityLinks;
        this.civilServantResourceFactory = civilServantResourceFactory;
        this.organisationalUnitRepository = organisationalUnitRepository;
    }

    @GetMapping
    public ResponseEntity<Resources<Void>> list() {
        log.debug("Listing civil servant links");

        Resources<Void> resource = new Resources<>(new ArrayList<>());
        resource.add(repositoryEntityLinks.linkToSingleResource(CivilServant.class, "me").withRel("me"));
        return ResponseEntity.ok(resource);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource<CivilServantResource>> get() {
        log.debug("Getting civil servant details for logged in user");

        return civilServantRepository.findByPrincipal().map(
                civilServant -> ResponseEntity.ok(civilServantResourceFactory.create(civilServant)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/manager")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ResponseEntity<Resource<CivilServantResource>> updateLineManager(@RequestParam(value = "email") String email) {

        Optional<CivilServant> optionalCivilServant = civilServantRepository.findByPrincipal();

        if (optionalCivilServant.isPresent()) {

            IdentityFromService lineManagerIdentity = lineManagerService.checkLineManager(email);
            if (lineManagerIdentity == null) {
                log.debug("Line manager email address not found in identity-service.");
                return ResponseEntity.notFound().build();
            }

            Optional<CivilServant> optionalLineManager = civilServantRepository.findByIdentity(lineManagerIdentity.getUid());
            if (!optionalLineManager.isPresent()) {
                log.debug("Line manager email address exists in identity-service, but no profile. uid = {}", lineManagerIdentity);
                return ResponseEntity.notFound().build();
            }

            CivilServant lineManager = optionalLineManager.get();
            CivilServant civilServant = optionalCivilServant.get();
            if (lineManager.equals(civilServant)) {
                log.info("User tried to set line manager to themself, {}.", civilServant);
                return ResponseEntity.badRequest().build();
            }

            civilServant.setLineManager(lineManager);
            civilServantRepository.save(civilServant);

            lineManagerService.notifyLineManager(civilServant, lineManager, email);

            return ResponseEntity.ok(civilServantResourceFactory.create(civilServant));
        }
        return ResponseEntity.unprocessableEntity().build();
    }

    @GetMapping("/org")
    public ResponseEntity<OrgCodeDTO> getOrgCodeForCivilServant(@RequestParam(value = "uid") String uid) {
        log.debug("Getting civil servant org details for user with uid " + uid);

        Optional<CivilServant> civilServant = civilServantRepository.findByIdentity(uid);

        if (civilServant.isPresent()) {
            return civilServantResourceFactory.getCivilServantOrganisationalUnitCode(civilServant.get())
                    .map(orgCodeDTO -> ResponseEntity.ok(orgCodeDTO))
                    .orElse(buildNotFound(uid));
        } else {
            return buildNotFound(uid);
        }

    }

    private ResponseEntity<OrgCodeDTO> buildNotFound(String uid) {
        log.warn(String.format("Civil Servant with uid %s not found", uid));
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/org")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity updateOrganisation(@Valid @RequestBody UpdateOrganisationDTO updateOrganisationDTO) {

        log.info("updating civil servants organisation for organisation=" + updateOrganisationDTO.getOrganisation());

        try {
            Optional<CivilServant> optionalCivilServant = civilServantRepository.findByPrincipal();
            if (optionalCivilServant.isPresent()) {
                Optional<OrganisationalUnit> newOrgUnit = organisationalUnitRepository.findByCode(updateOrganisationDTO.getOrganisation());
                if (newOrgUnit.isPresent()) {
                    CivilServant civilServant = optionalCivilServant.get();
                    civilServant.setOrganisationalUnit(newOrgUnit.get());
                    civilServantRepository.save(civilServant);
                    return ResponseEntity.noContent().build();
                } else {
                    log.warn(String.format("Organisation to update with code %s has not been found", updateOrganisationDTO.getOrganisation()));
                    return ResponseEntity.notFound().build();
                }

            } else {
                log.warn("civil servant to update has not been found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("An error occurred updating Civil Servants organisation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/org")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity removeOrganisation() {
        /*
         * separate end point to make the civil servants organisation null.
         * This is so we don't allow a non-existent org in the update scenario.
         */
        log.info("deleting civil servants organisation for organisation");

        try {
            Optional<CivilServant> optionalCivilServant = civilServantRepository.findByPrincipal();
            if (optionalCivilServant.isPresent()) {
                CivilServant civilServant = optionalCivilServant.get();
                civilServant.setOrganisationalUnit(null);
                civilServantRepository.save(civilServant);
                return ResponseEntity.noContent().build();
            } else {
                log.warn("civil servant to update has not been found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("An error occurred deleting Civil Servants organisation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{uid}/delete")
    @PreAuthorize("hasAnyAuthority('IDENTITY_DELETE', 'CLIENT')")
    @Transactional
    public ResponseEntity deleteCivilServant(@PathVariable String uid) {
        civilServantRepository.findByIdentity(uid).ifPresent(civilServant -> civilServantRepository.delete(civilServant));

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/org/reset")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getForceOrgChangeFlag() {

        log.info("getting civil servants force org change flag");

        try {
            Optional<CivilServant> optionalCivilServant = civilServantRepository.findByPrincipal();
            if (optionalCivilServant.isPresent()) {
                CivilServant civilServant = optionalCivilServant.get();
                return ResponseEntity.ok(civilServant.getForceOrgReset());
            } else {
                log.warn("civil servant to update has not been found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("An error occurred updating Civil Servants force org change flag", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/org/reset")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity updateForceOrgChangeFlag(@Valid @RequestBody UpdateForceOrgChangeDTO updateForceOrgChangeDTO) {

        log.info("updating civil servants force org change flag to=" + updateForceOrgChangeDTO.isForceOrgChange());

        try {
            Optional<CivilServant> optionalCivilServant = civilServantRepository.findByPrincipal();
            if (optionalCivilServant.isPresent()) {
                CivilServant civilServant = optionalCivilServant.get();
                civilServant.setForceOrgReset(updateForceOrgChangeDTO.isForceOrgChange());
                civilServantRepository.save(civilServant);
                return ResponseEntity.noContent().build();
            } else {
                log.warn("civil servant to update has not been found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("An error occurred updating Civil Servants force org change flag", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public RepositoryLinksResource process(RepositoryLinksResource resource) {
        resource.add(ControllerLinkBuilder.linkTo(CivilServantController.class).withRel("civilServants"));
        return resource;
    }

}
