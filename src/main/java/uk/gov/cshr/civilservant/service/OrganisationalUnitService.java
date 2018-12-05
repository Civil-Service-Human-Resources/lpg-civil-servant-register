package uk.gov.cshr.civilservant.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.dto.OrganisationalUnitDto;
import uk.gov.cshr.civilservant.dto.factory.OrganisationalUnitDtoFactory;
import uk.gov.cshr.civilservant.repository.OrganisationalUnitRepository;

@Service
@Transactional
public class OrganisationalUnitService extends SelfReferencingEntityService<OrganisationalUnit, OrganisationalUnitDto> {

    public OrganisationalUnitService(OrganisationalUnitRepository organisationalUnitRepository,
                                     RepositoryEntityService<OrganisationalUnit> repositoryEntityService, OrganisationalUnitDtoFactory organisationalUnitDtoFactory) {
        super(organisationalUnitRepository, repositoryEntityService, organisationalUnitDtoFactory);
    }
}
