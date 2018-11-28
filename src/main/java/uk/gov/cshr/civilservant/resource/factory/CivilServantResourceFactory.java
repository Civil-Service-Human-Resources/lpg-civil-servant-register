package uk.gov.cshr.civilservant.resource.factory;

import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Component;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.resource.CivilServantResource;
import uk.gov.cshr.civilservant.service.identity.IdentityService;

@Component
public class CivilServantResourceFactory {

    private final RepositoryEntityLinks repositoryEntityLinks;
    private final IdentityService identityService;
    private final LinkFactory linkFactory;

    public CivilServantResourceFactory(RepositoryEntityLinks repositoryEntityLinks, IdentityService identityService, LinkFactory linkFactory) {
        this.repositoryEntityLinks = repositoryEntityLinks;
        this.identityService = identityService;
        this.linkFactory = linkFactory;
    }

    public Resource<CivilServantResource> create(CivilServant civilServant) {

        String lineManagerEmail = identityService.getEmailAddress(civilServant.getLineManager());

        Resource<CivilServantResource> resource =
                new Resource<>(new CivilServantResource(civilServant, lineManagerEmail));

        resource.add(linkFactory.createSelfLink(civilServant));
        resource.add(linkFactory.createRelationshipLink(civilServant, "organisationalUnit"));
        resource.add(linkFactory.createRelationshipLink(civilServant, "grade"));
        resource.add(linkFactory.createRelationshipLink(civilServant, "profession"));

        return resource;
    }
}
