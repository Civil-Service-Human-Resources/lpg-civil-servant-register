package uk.gov.cshr.civilservant.service;

import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.stereotype.Service;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;

@Service
public class RepositoryEntityService {

    private RepositoryEntityLinks repositoryEntityLinks;

    public RepositoryEntityService(RepositoryEntityLinks repositoryEntityLinks) {
        this.repositoryEntityLinks = repositoryEntityLinks;
    }

    public String getUriFromOrganisationalUnit(OrganisationalUnit org) {
        return repositoryEntityLinks.linkFor(OrganisationalUnit.class).slash(org.getId()).toUri().toString();
    }
}
