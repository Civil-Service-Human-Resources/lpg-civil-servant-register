package uk.gov.cshr.civilservant.dto.factory;

import org.springframework.stereotype.Component;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.domain.Profession;
import uk.gov.cshr.civilservant.dto.OrganisationalUnitDto;
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
        professionDto.setName(formatName(profession));
        professionDto.setUrl(repositoryEntityService.getUri(profession));

        return professionDto;
    }


    /**
     * Format the name of an profession to be prefixed with parental hierarchy.
     *
     * e.g. Parent | Child | Subchild
     */
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
