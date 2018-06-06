package uk.gov.cshr.civilservant.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;
import uk.gov.cshr.civilservant.resource.CivilServantResource;
import uk.gov.cshr.civilservant.service.LineManagerService;
import uk.gov.cshr.civilservant.service.NotifyService;
import uk.gov.cshr.civilservant.service.identity.IdentityFromService;
import uk.gov.cshr.civilservant.service.identity.IdentityService;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

@RepositoryRestController
@RequestMapping("/civilServants")
public class CivilServantController implements ResourceProcessor<RepositoryLinksResource> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CivilServantController.class);

    private LineManagerService lineManagerService;


    private IdentityService identityService;

    private CivilServantRepository civilServantRepository;

    private NotifyService notifyService;

    private RepositoryEntityLinks repositoryEntityLinks;


    @Autowired
    public CivilServantController(CivilServantRepository civilServantRepository,
                                  RepositoryEntityLinks repositoryEntityLinks,
                                  LineManagerService lineManagerService) {
        checkArgument(civilServantRepository != null);
        checkArgument(repositoryEntityLinks != null);
        this.civilServantRepository = civilServantRepository;
        this.repositoryEntityLinks = repositoryEntityLinks;


        this.lineManagerService = lineManagerService;
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

        Optional<CivilServant> optionalCivilServant = civilServantRepository.findByPrincipal();

        return getResourceResponseEntity(optionalCivilServant);
    }

    @PatchMapping("/manager")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource<CivilServantResource>> check(@RequestParam(value = "email") String email) throws NotificationClientException {
        IdentityFromService lineManager = new IdentityFromService();

        Optional<CivilServant> optionalCivilServant = civilServantRepository.findByPrincipal();

        if (optionalCivilServant.isPresent()) {
            lineManager = lineManagerService.checkLineManager(email);


            if (lineManager != null) {
                CivilServant civilServant = optionalCivilServant.get();

                civilServant = lineManagerService.UpdateAndNotifyLineManager(civilServant, lineManager, email);

                if (civilServant == null) {
                    return ResponseEntity.badRequest().build();
                } else {
                    return getResourceResponseEntity(optionalCivilServant);
                }
            }

            // line manager not found
            return ResponseEntity.notFound().build();
        }
        // can't find current user record
        return ResponseEntity.unprocessableEntity().build();
    }


    @Override
    public RepositoryLinksResource process(RepositoryLinksResource resource) {
        resource.add(ControllerLinkBuilder.linkTo(CivilServantController.class).withRel("civilServants"));
        return resource;
    }


    private ResponseEntity<Resource<CivilServantResource>> getResourceResponseEntity(Optional<CivilServant> optionalCivilServant) {
        return optionalCivilServant
                .map(civilServant -> {
                    Resource<CivilServantResource> resource = new Resource<>(new CivilServantResource(civilServant));
                    resource.add(repositoryEntityLinks.linkToSingleResource(CivilServant.class, civilServant.getId()).withSelfRel());
                    resource.add(repositoryEntityLinks.linkFor(CivilServant.class).slash(civilServant.getId()).slash("organisation").withRel("organisation"));
                    resource.add(repositoryEntityLinks.linkFor(CivilServant.class).slash(civilServant.getId()).slash("grade").withRel("grade"));
                    resource.add(repositoryEntityLinks.linkFor(CivilServant.class).slash(civilServant.getId()).slash("profession").withRel("profession"));
                    resource.add(repositoryEntityLinks.linkFor(CivilServant.class).slash(civilServant.getId()).slash("jobRole").withRel("jobRole"));
                    return ResponseEntity.ok(resource);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
