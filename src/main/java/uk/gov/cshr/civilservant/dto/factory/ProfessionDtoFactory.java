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
        professionDto.setFormattedName(formatName(profession));
        professionDto.setHref(repositoryEntityService.getUri(profession));
        professionDto.setId(profession.getId());

        return professionDto;
    }

    String formatName(Profession profession) {
        Profession currentNode = profession;

        String name = currentNode.getName();

        while (currentNode.hasParent()) {
            currentNode = currentNode.getParent();

            StringBuilder sb = new StringBuilder();
            name = sb.append(currentNode.getName())
                    .append(" | ")
                    .append(name)
                    .toString();
        }

        return name;
    }
}
