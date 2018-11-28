package uk.gov.cshr.civilservant.service;

import org.springframework.stereotype.Service;
import uk.gov.cshr.civilservant.domain.Profession;
import uk.gov.cshr.civilservant.repository.ProfessionRepository;

@Service
public class ProfessionService extends SelfReferencingEntityService<Profession> {
    public ProfessionService(ProfessionRepository professionRepository, RepositoryEntityService<Profession> repositoryEntityService) {
        super(professionRepository, repositoryEntityService);
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
