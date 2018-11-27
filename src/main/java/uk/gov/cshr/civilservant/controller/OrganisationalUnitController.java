package uk.gov.cshr.civilservant.controller;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.service.OrganisationalUnitService;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

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

    @RequestMapping(value = "/{organisationalUnitId}")
    public ResponseEntity<OrganisationalUnit> getOrganisationalUnit(@PathVariable final Long organisationalUnitId) {
        return organisationalUnitService.getOrganisationalUnitById(organisationalUnitId)
                .map(o -> new ResponseEntity<>(o, OK))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }
}
