package uk.gov.cshr.civilservant.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;
import uk.gov.cshr.civilservant.resource.CivilServantResource;
import uk.gov.cshr.civilservant.resource.factory.CivilServantResourceFactory;
import uk.gov.cshr.civilservant.service.LineManagerService;
import uk.gov.cshr.civilservant.service.identity.IdentityFromService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RepositoryRestController
@RequestMapping("/civilServants")
@RestResource(exported = false)
public class CivilServantController implements ResourceProcessor<RepositoryLinksResource> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CivilServantController.class);

    private final LineManagerService lineManagerService;

    private final CivilServantRepository civilServantRepository;

    private final RepositoryEntityLinks repositoryEntityLinks;

    private final CivilServantResourceFactory civilServantResourceFactory;

    public CivilServantController(LineManagerService lineManagerService, CivilServantRepository civilServantRepository,
                                  RepositoryEntityLinks repositoryEntityLinks,
                                  CivilServantResourceFactory civilServantResourceFactory) {
        this.lineManagerService = lineManagerService;
        this.civilServantRepository = civilServantRepository;
        this.repositoryEntityLinks = repositoryEntityLinks;
        this.civilServantResourceFactory = civilServantResourceFactory;
    }

    @GetMapping
    public ResponseEntity<Resources<Void>> list() {
        LOGGER.debug("Listing civil servant links");

        Resources<Void> resource = new Resources<>(new ArrayList<>());
        resource.add(repositoryEntityLinks.linkToSingleResource(CivilServant.class, "me").withRel("me"));
        return ResponseEntity.ok(resource);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource<CivilServantResource>> get() {
        LOGGER.debug("Getting civil servant details for logged in user");

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
                LOGGER.debug("Line manager email address not found in identity-service.");
                return ResponseEntity.notFound().build();
            }

            Optional<CivilServant> optionalLineManager = civilServantRepository.findByIdentity(lineManagerIdentity.getUid());
            if (!optionalLineManager.isPresent()) {
                LOGGER.debug("Line manager email address exists in identity-service, but no profile. uid = {}", lineManagerIdentity);
                return ResponseEntity.notFound().build();
            }

            CivilServant lineManager = optionalLineManager.get();
            CivilServant civilServant = optionalCivilServant.get();
            if (lineManager.equals(civilServant)) {
                LOGGER.info("User tried to set line manager to themself, {}.", civilServant);
                return ResponseEntity.badRequest().build();
            }

            civilServant.setLineManager(lineManager);
            civilServantRepository.save(civilServant);

            lineManagerService.notifyLineManager(civilServant, lineManager, email);

            return ResponseEntity.ok(civilServantResourceFactory.create(civilServant));
        }
        return ResponseEntity.unprocessableEntity().build();
    }

    @DeleteMapping("/{uid}/delete")
    @PreAuthorize("hasAnyAuthority('IDENTITY_DELETE', 'CLIENT')")
    @Transactional
    public ResponseEntity deleteCivilServant(@PathVariable String uid) {
        civilServantRepository.findByIdentity(uid).ifPresent(civilServant -> civilServantRepository.delete(civilServant));

        return ResponseEntity.noContent().build();
    }


    @Override
    public RepositoryLinksResource process(RepositoryLinksResource resource) {
        resource.add(ControllerLinkBuilder.linkTo(CivilServantController.class).withRel("civilServants"));
        return resource;
    }

    @GetMapping("/civilservantwithreportingpermission")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getCivilServantUIDsWithReportingPermission() {
        List<String> listUid = civilServantRepository.findCivilServantUID();
        return ResponseEntity.ok(listUid);
    }

    @GetMapping("/civilservantreportingpermission/{uid}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getCivilServantReportingPermission(@PathVariable String uid) {
        List<String> listUid = civilServantRepository.findCivilServantReportingPermission(uid);
        return ResponseEntity.ok(listUid);
    }}
