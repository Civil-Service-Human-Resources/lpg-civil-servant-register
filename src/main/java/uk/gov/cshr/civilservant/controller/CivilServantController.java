package uk.gov.cshr.civilservant.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;
import uk.gov.cshr.civilservant.resource.CivilServantResource;
import uk.gov.cshr.civilservant.service.identity.IdentityFromService;
import uk.gov.cshr.civilservant.service.identity.IdentityService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

@RepositoryRestController
@RequestMapping("/civilServants")
public class CivilServantController implements ResourceProcessor<RepositoryLinksResource> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CivilServantController.class);


    private IdentityService identityService;

    private CivilServantRepository civilServantRepository;

    private RepositoryEntityLinks repositoryEntityLinks;

    @Autowired
    public CivilServantController(CivilServantRepository civilServantRepository,
                                  RepositoryEntityLinks repositoryEntityLinks,
                                  IdentityService identityService) {
        checkArgument(civilServantRepository != null);
        checkArgument(repositoryEntityLinks != null);
        this.civilServantRepository = civilServantRepository;
        this.repositoryEntityLinks = repositoryEntityLinks;
        this.identityService = identityService;
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
    public ResponseEntity<Resources<Void>> check() {

        Resources<Void> resource = new Resources<>(new ArrayList<>());



        Collection<IdentityFromService> identities = identityService.listAll();

        for (IdentityFromService identity : identities) {
            LOGGER.info("Got identity with uid {} and username {}", identity.getUid(), identity.getUsername());
        }

        return ResponseEntity.ok(resource);
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
