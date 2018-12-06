package uk.gov.cshr.civilservant.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.repository.OrganisationalUnitRepository;

@Service
@Transactional
public class OrganisationalUnitService extends SelfReferencingEntityService<OrganisationalUnit> {

    public OrganisationalUnitService(OrganisationalUnitRepository organisationalUnitRepository,
                                     RepositoryEntityService<OrganisationalUnit> repositoryEntityService) {
        super(organisationalUnitRepository, repositoryEntityService);
    }

    /**
     * Format the name of an organisationalUnit to be prefixed with parental hierarchy.
     * <p>
     * e.g. Cabinet Office (CO) | Child (C) | Subchild (SC)
     */
    String formatName(OrganisationalUnit organisationalUnit) {
        OrganisationalUnit currentNode = organisationalUnit;

        String name = currentNode.getName() + formatAbbreviationForNode(currentNode);

        while (currentNode.hasParent()) {
            currentNode = currentNode.getParent();

            StringBuilder sb = new StringBuilder();
            name = sb.append(currentNode.getName())
                    .append(formatAbbreviationForNode(currentNode))
                    .append(" | ")
                    .append(name)
                    .toString();
        }

        return name;
    }

    /**
     * If an organisational unit has an abbreviation, we should format it to be surrounded by parenthesis,
     * Otherwise, we should leave as blank
     *
     * e.g:
     * With abbreviation -> Cabinet Office (CO) | Child (C) | Subchild (SC)
     * With no abbreviation -> Cabinet Office | Child | Subchild
     */
    private String formatAbbreviationForNode(OrganisationalUnit node) {
        return (node.getAbbreviation() != null && node.getAbbreviation() != "") ? " (" + node.getAbbreviation() + ")" : "";
    }
}
