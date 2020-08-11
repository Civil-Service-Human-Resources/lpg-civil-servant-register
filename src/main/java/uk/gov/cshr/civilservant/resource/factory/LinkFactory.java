package uk.gov.cshr.civilservant.resource.factory;

import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;
import uk.gov.cshr.civilservant.domain.RegistryEntity;

@Component
public class LinkFactory {

  private final RepositoryEntityLinks repositoryEntityLinks;

  public LinkFactory(RepositoryEntityLinks repositoryEntityLinks) {
    this.repositoryEntityLinks = repositoryEntityLinks;
  }

  public Link createRelationshipLink(RegistryEntity entity, String relationship) {
    return repositoryEntityLinks
        .linkFor(entity.getClass())
        .slash(entity.getId())
        .slash(relationship)
        .withRel(relationship);
  }

  public Link createSelfLink(RegistryEntity entity) {
    return repositoryEntityLinks
        .linkToSingleResource(entity.getClass(), entity.getId())
        .withSelfRel();
  }
}
