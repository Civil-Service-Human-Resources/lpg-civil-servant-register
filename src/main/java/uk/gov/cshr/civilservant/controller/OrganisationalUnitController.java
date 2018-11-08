package uk.gov.cshr.civilservant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.repository.OrganisationalUnitRepository;
import uk.gov.cshr.civilservant.service.OrganisationalUnitService;

import java.util.Map;

@RepositoryRestController
@RequestMapping("/organisationalUnits")
public class OrganisationalUnitController {

    private OrganisationalUnitRepository organisationalUnitRepository;

    private OrganisationalUnitService organisationalUnitService;

    @Autowired
    public OrganisationalUnitController(OrganisationalUnitRepository organisationalUnitRepository, OrganisationalUnitService organisationalUnitService) {
        this.organisationalUnitRepository = organisationalUnitRepository;
        this.organisationalUnitService = organisationalUnitService;
    }

    @GetMapping("/tree")
    public ResponseEntity<Iterable<OrganisationalUnit>> listOrganisationalUnitsAsTreeStructure() {
        Iterable<OrganisationalUnit> organisationalUnits = organisationalUnitRepository.findAll();

        return ResponseEntity.ok(organisationalUnits);
    }

    @GetMapping("/flat")
    public ResponseEntity<Map<String, String>> listOrganisationalUnitsAsFlatStructure() {
        Iterable<OrganisationalUnit> organisationalUnits = organisationalUnitRepository.findAll();

        Map<String, String> organisationalUnitsMap = organisationalUnitService.getOrganisationalUnitsMap(organisationalUnits);

        return ResponseEntity.ok(organisationalUnitsMap);
    }
}
