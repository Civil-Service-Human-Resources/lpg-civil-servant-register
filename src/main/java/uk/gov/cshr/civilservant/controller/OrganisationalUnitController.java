package uk.gov.cshr.civilservant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.service.OrganisationalUnitService;

import java.util.ArrayList;
import java.util.Map;

@RepositoryRestController
@RequestMapping("/organisationalUnits")
public class OrganisationalUnitController {

    private OrganisationalUnitService organisationalUnitService;

    @Autowired
    public OrganisationalUnitController(OrganisationalUnitService organisationalUnitService) {
        this.organisationalUnitService = organisationalUnitService;
    }

    @GetMapping
    public ResponseEntity<ArrayList<OrganisationalUnit>> listOrganisationalUnitsAsTreeStructure(@PathVariable("tpye") String tpye) {
        ArrayList<OrganisationalUnit> organisationalUnits = organisationalUnitService.getParentOrganisationalUnits();

        return ResponseEntity.ok(organisationalUnits);
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> listOrganisationalUnitsAsFlatStructure(@PathVariable("tpye") String type) {
        Map<String, String> organisationalUnitsMap = organisationalUnitService.getOrganisationalUnitsMap();

        return ResponseEntity.ok(organisationalUnitsMap);
    }

    @GetMapping("/{code}")
    public ResponseEntity<OrganisationalUnit> getByCode(@PathVariable("code") String code) {
        OrganisationalUnit organisationalUnits = organisationalUnitService.findByCode(code);

        return ResponseEntity.ok(organisationalUnits);
    }
}
