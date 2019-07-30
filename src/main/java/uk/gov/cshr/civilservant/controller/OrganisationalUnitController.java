package uk.gov.cshr.civilservant.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.dto.OrganisationalUnitDto;
import uk.gov.cshr.civilservant.service.OrganisationalUnitService;

import java.util.List;

@RepositoryRestController
@RequestMapping("/organisationalUnits")
public class OrganisationalUnitController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrganisationalUnitController.class);

    private OrganisationalUnitService organisationalUnitService;

    public OrganisationalUnitController(OrganisationalUnitService organisationalUnitService) {
        this.organisationalUnitService = organisationalUnitService;
    }

    @PostMapping
    public ResponseEntity<OrganisationalUnit> saveOrgUnit(@RequestBody OrganisationalUnit organisationalUnit) {
        LOGGER.info("Getting org tree");
        OrganisationalUnit organisationalUnits = organisationalUnitService.save(organisationalUnit);

        new Thread(this::listOrganisationalUnitsAsFlatStructure).start();
        new Thread(this::listOrganisationalUnitsAsTreeStructure).start();

        return ResponseEntity.ok(organisationalUnits);
    }

    @GetMapping("/tree")
    @Cacheable("organisationalUnitsTree")
    public ResponseEntity<List<OrganisationalUnit>> listOrganisationalUnitsAsTreeStructure() {
        LOGGER.info("Getting org tree");
        List<OrganisationalUnit> organisationalUnits = organisationalUnitService.getParents();

        return ResponseEntity.ok(organisationalUnits);
    }

    @GetMapping("/flat")
    @Cacheable("organisationalUnitsFlat")
    public ResponseEntity<List<OrganisationalUnitDto>> listOrganisationalUnitsAsFlatStructure() {
        LOGGER.info("Getting org flat");
        List<OrganisationalUnitDto> organisationalUnitsMap = organisationalUnitService.getListSortedByValue();

        return ResponseEntity.ok(organisationalUnitsMap);
    }

    @GetMapping("/parent/{code}")
    public ResponseEntity<List<OrganisationalUnit>> getOrganisationWithParents(@PathVariable String code) {
        return ResponseEntity.ok(organisationalUnitService.getOrganisationWithParents(code));
    }

    @GetMapping("/normalised")
    public ResponseEntity<List<OrganisationalUnit>> getOrganisationNormalised() {
        return ResponseEntity.ok(organisationalUnitService.getOrganisationsNormalised());
    }
}

