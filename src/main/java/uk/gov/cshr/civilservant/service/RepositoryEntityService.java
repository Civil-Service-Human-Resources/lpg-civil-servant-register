package uk.gov.cshr.civilservant.service;

import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.stereotype.Service;
import uk.gov.cshr.civilservant.domain.RegistryEntity;

@Service
public class RepositoryEntityService<T extends RegistryEntity> {

    private RepositoryEntityLinks repositoryEntityLinks;

    public RepositoryEntityService(RepositoryEntityLinks repositoryEntityLinks) {
        this.repositoryEntityLinks = repositoryEntityLinks;
    }

    public String getUri(Class<T> type, T entity) {
        return repositoryEntityLinks.linkFor(type).slash(entity.getId()).toUri().toString();
    }
}
