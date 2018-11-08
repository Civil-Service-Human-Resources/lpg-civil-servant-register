package uk.gov.cshr.civilservant.service;

import org.springframework.stereotype.Service;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class OrganisationalUnitService {

    public Map<String, String> getOrganisationalUnitsMap(Iterable<OrganisationalUnit> organisationalUnits) {
        Map<String, String> organisationalUnitsMap = new LinkedHashMap<>();

        organisationalUnits.forEach(organisationalUnit -> {
            OrganisationalUnit currentNode = organisationalUnit;

            String name = currentNode.getName() + formatAbbreviationForNode(currentNode);

            while (currentNode.hasParent()) {
                currentNode = currentNode.getParent();
                name = currentNode.getName() + formatAbbreviationForNode(currentNode) + ", " + name;
            }

            organisationalUnitsMap.put(organisationalUnit.getCode(), name);
        });

        return organisationalUnitsMap;
    }

    private String formatAbbreviationForNode(OrganisationalUnit node) {
        String formattedAbbreviation = node.getAbbreviation() != null ? " (" + node.getAbbreviation() + ")" : "";

        return formattedAbbreviation;
    }
}
