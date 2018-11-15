package uk.gov.cshr.civilservant.controller;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.service.OrganisationalUnitService;

import java.util.List;
import java.util.Map;

@RepositoryRestController
@RequestMapping("/organisationalUnits")
public class OrganisationalUnitController {

    private OrganisationalUnitService organisationalUnitService;

    public OrganisationalUnitController(OrganisationalUnitService organisationalUnitService) {
        this.organisationalUnitService = organisationalUnitService;
    }

    @GetMapping("/tree")
    public ResponseEntity<List<OrganisationalUnit>> listOrganisationalUnitsAsTreeStructure() {
        List<OrganisationalUnit> organisationalUnits = organisationalUnitService.getParentOrganisationalUnits();

        return ResponseEntity.ok(organisationalUnits);
    }

    @GetMapping("/flat")
    public ResponseEntity<Map<String, String>> listOrganisationalUnitsAsFlatStructure() {
        Map<String, String> organisationalUnitsMap = organisationalUnitService.getOrganisationalUnitsMapSortedByValue();

        return ResponseEntity.ok(organisationalUnitsMap);
    }
}
