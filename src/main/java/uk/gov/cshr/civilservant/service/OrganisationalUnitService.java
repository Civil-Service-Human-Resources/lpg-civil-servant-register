package uk.gov.cshr.civilservant.service;

import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.repository.OrganisationalUnitRepository;

import java.util.*;
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

    /**
     * This will return all Parent organisations with any sub-organisations as a list
     */
    public List<OrganisationalUnit> getParentOrganisationalUnits() {
        ArrayList<OrganisationalUnit> organisationalUnitArrayList = new ArrayList<>();

        organisationalUnitRepository.findAll().forEach(organisationalUnit -> {
            OrganisationalUnit currentNode = organisationalUnit;
            if (!currentNode.hasParent()) {
                organisationalUnitArrayList.add(currentNode);
            }
        });

        return organisationalUnitArrayList;
    }


    /**
     * This will return all Organisations as a map.
     * In the map, the key will be the href of the organisation which is obtained using {@link RepositoryEntityLinks} in {@link RepositoryEntityService}.
     * The value will represent the organisation name, but formatted to include any parents recursively using {@link OrganisationalUnitService#formatName(OrganisationalUnit)}.
     * Finally, this is map is sorted by value into a LinkedHashMap
     */
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

    /**
     * Format the name of an organisationalUnit to be prefixed with parental hierarchy.
     *
     * e.g. Cabinet Office (CO) | Child (C) | Subchild (SC)
     */
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

    /**
     * If an organisational unit has an abbreviation, we should format it to be surrounded by parenthesis,
     * Otherwise, we should leave as blank
     *
     * e.g:
     * With abbreviation -> Cabinet Office (CO) | Child (C) | Subchild (SC)
     * With no abbreviation -> Cabinet Office | Child | Subchild
     */
    private String formatAbbreviationForNode(OrganisationalUnit node) {
        return node.getAbbreviation() != null ? " (" + node.getAbbreviation() + ")" : "";
    }
}
