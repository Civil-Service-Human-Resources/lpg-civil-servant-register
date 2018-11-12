package uk.gov.cshr.civilservant.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.repository.OrganisationalUnitRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class OrganisationalUnitService {

    OrganisationalUnitRepository organisationalUnitRepository;

    @Autowired
    public OrganisationalUnitService(OrganisationalUnitRepository organisationalUnitRepository) {
        this.organisationalUnitRepository = organisationalUnitRepository;
    }

    public ArrayList<OrganisationalUnit> getParentOrganisationalUnits() {
        ArrayList<OrganisationalUnit> organisationalUnitArrayList = new ArrayList<>();

        Iterable<OrganisationalUnit> organisationalUnits = organisationalUnitRepository.findAll();

        organisationalUnits.forEach(organisationalUnit -> {
            OrganisationalUnit currentNode = organisationalUnit;
            if (!currentNode.hasParent()) {
                organisationalUnitArrayList.add(currentNode);
            }
        });

        return organisationalUnitArrayList;
    }

    public Map<String, String> getOrganisationalUnitsMap() {
        Map<String, String> organisationalUnitsMap = new LinkedHashMap<>();

        Iterable<OrganisationalUnit> organisationalUnits = organisationalUnitRepository.findAll();

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
