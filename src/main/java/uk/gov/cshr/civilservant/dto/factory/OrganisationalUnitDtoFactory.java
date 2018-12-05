package uk.gov.cshr.civilservant.dto.factory;

import org.springframework.stereotype.Component;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.dto.DtoFactory;
import uk.gov.cshr.civilservant.dto.OrganisationalUnitDto;
import uk.gov.cshr.civilservant.service.RepositoryEntityService;

@Component
public class OrganisationalUnitDtoFactory extends DtoFactory<OrganisationalUnitDto, OrganisationalUnit> {

    private RepositoryEntityService<OrganisationalUnit> repositoryEntityService;


    public OrganisationalUnitDtoFactory(RepositoryEntityService repositoryEntityService) {
        this.repositoryEntityService = repositoryEntityService;
    }

    public OrganisationalUnitDto create(OrganisationalUnit organisationalUnit) {
        OrganisationalUnitDto organisationalUnitDto = new OrganisationalUnitDto();
        organisationalUnitDto.setCode(organisationalUnit.getCode());
        organisationalUnitDto.setName(organisationalUnit.getName());
        organisationalUnitDto.setUrl(repositoryEntityService.getUri(organisationalUnit));

        return organisationalUnitDto;
    }
}
