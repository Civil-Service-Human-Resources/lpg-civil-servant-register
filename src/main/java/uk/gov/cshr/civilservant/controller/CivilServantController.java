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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;
import uk.gov.cshr.civilservant.resource.CivilServantResource;
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


    private IdentityService identityService;

    private CivilServantRepository civilServantRepository;

    private NotifyService notifyService;

    private RepositoryEntityLinks repositoryEntityLinks;

    @Value("${govNotify.template.lineManager}")
    private String govNotifyLineManagerTemplateId;

    @Autowired
    public CivilServantController(CivilServantRepository civilServantRepository,
                                  RepositoryEntityLinks repositoryEntityLinks,
                                  IdentityService identityService,
                                  NotifyService notifyService) {
        checkArgument(civilServantRepository != null);
        checkArgument(repositoryEntityLinks != null);
        this.civilServantRepository = civilServantRepository;
        this.repositoryEntityLinks = repositoryEntityLinks;
        this.identityService = identityService;
        this.notifyService = notifyService;
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

    @GetMapping("/manager")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource<CivilServantResource>> check(@RequestParam(value = "email") String email) throws NotificationClientException {
        IdentityFromService lineManager = new IdentityFromService();

        Optional<CivilServant> optionalCivilServant = civilServantRepository.findByPrincipal();

        if (optionalCivilServant.isPresent()) {
            CivilServant civilServant =  optionalCivilServant.get();

            lineManager = identityService.findByEmail(email);
            if (lineManager != null) {
                    // check to see if line manager is the same person
                    if (email.equals(civilServant.getIdentity().getUid())) {
                        // you can't be your own line manager
                        return ResponseEntity.badRequest().build();
                    } else {
                        // update and save
                        civilServant.setLineManagerUid(lineManager.getUid());
                        civilServant.setLineManagerEmail(lineManager.getUsername());
                        civilServantRepository.save(civilServant);
                        // now notify
                        Optional<CivilServant> optionalLineManager = civilServantRepository.findByIdentity(lineManager.getUid());

                        String lineManagerName = "";

                        if (optionalLineManager.isPresent()) {
                            CivilServant lineManagerProfile = optionalLineManager.get();
                            lineManagerName =lineManagerProfile.getFullName();
                        }

                        String learnerName = civilServant.getFullName();
                        if (learnerName == null) {
                            learnerName = "";
                        }

                        notifyService.notify(email, govNotifyLineManagerTemplateId, lineManagerName,learnerName);
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
