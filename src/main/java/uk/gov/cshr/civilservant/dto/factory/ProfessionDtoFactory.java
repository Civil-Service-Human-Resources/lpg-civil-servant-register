package uk.gov.cshr.civilservant.dto.factory;

import org.springframework.stereotype.Component;
import uk.gov.cshr.civilservant.domain.Profession;
import uk.gov.cshr.civilservant.dto.ProfessionDto;
import uk.gov.cshr.civilservant.service.RepositoryEntityService;

@Component
public class ProfessionDtoFactory extends DtoFactory<ProfessionDto, Profession> {

    private RepositoryEntityService<Profession> repositoryEntityService;


    public ProfessionDtoFactory(RepositoryEntityService repositoryEntityService) {
        this.repositoryEntityService = repositoryEntityService;
    }

    public ProfessionDto create(Profession profession) {
        ProfessionDto professionDto = new ProfessionDto();
        professionDto.setName(profession.getName());
        professionDto.setFormattedName(profession.getName());
        professionDto.setHref(repositoryEntityService.getUri(profession));

        return professionDto;
    }
}
