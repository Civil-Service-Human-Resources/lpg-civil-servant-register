package uk.gov.cshr.civilservant.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.repository.OrganisationalUnitRepository;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrganisationalUnitService {

    private OrganisationalUnitRepository organisationalUnitRepository;

    private RepositoryEntityService repositoryEntityService;

    public OrganisationalUnitService(OrganisationalUnitRepository organisationalUnitRepository, RepositoryEntityService repositoryEntityService) {
        this.organisationalUnitRepository = organisationalUnitRepository;
        this.repositoryEntityService = repositoryEntityService;
    }

    public List<OrganisationalUnit> getParentOrganisationalUnits() {
        return organisationalUnitRepository.findAll()
                .stream()
                .sequential()
                .filter(org -> !org.hasParent())
                .collect(Collectors.toList());
    }

    public Map<String, String> getOrganisationalUnitsMapSortedByValue() {
        return organisationalUnitRepository.findAll().stream()
                .collect(Collectors.toMap(org -> repositoryEntityService.getUriFromOrganisationalUnit(org), this::formatName))
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (u, v) -> {
                            throw new IllegalStateException(String.format("Duplicate key %s", u));
                        },
                        LinkedHashMap::new));
    }

    private String formatName(OrganisationalUnit organisationalUnit) {
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

    private String formatAbbreviationForNode(OrganisationalUnit node) {
        /*
         * If an organisational unit has an abbreviation, we should format it to be surrounded by parenthesis,
         * Otherwise, we should leave as blank
         * e.g:
         *   With abbreviation -> Cabinet Office (CO) | Child (C) | Subchild (SC)
         *   With no abbreviation -> Cabinet Office | Child | Subchild
         * */

        return node.getAbbreviation() != null ? " (" + node.getAbbreviation() + ")" : "";
    }
}
