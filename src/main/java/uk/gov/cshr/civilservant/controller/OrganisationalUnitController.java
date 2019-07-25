package uk.gov.cshr.civilservant.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.dto.OrganisationalUnitDto;
import uk.gov.cshr.civilservant.service.OrganisationalUnitService;

import java.util.List;

@RepositoryRestController
@RequestMapping("/organisationalUnits")
@CacheConfig(cacheNames = {"organisationalUnits"})
public class OrganisationalUnitController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrganisationalUnitController.class);

    private OrganisationalUnitService organisationalUnitService;

    public OrganisationalUnitController(OrganisationalUnitService organisationalUnitService) {
        this.organisationalUnitService = organisationalUnitService;
    }

    @GetMapping("/tree")
    @Cacheable
    public ResponseEntity<List<OrganisationalUnit>> listOrganisationalUnitsAsTreeStructure() {
        LOGGER.info("Getting org tree");
        List<OrganisationalUnit> organisationalUnits = organisationalUnitService.getParents();

        return ResponseEntity.ok(organisationalUnits);
    }

    @GetMapping("/flat")
    @Cacheable
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

    @PostMapping
    @CacheEvict(value = "organisationalUnits", allEntries = true)
    public ResponseEntity<Void> save(@RequestBody OrganisationalUnit organisationalUnit, UriComponentsBuilder builder) {
        LOGGER.info("Saving org {}", organisationalUnit.toString());

        OrganisationalUnit save = organisationalUnitService.save(organisationalUnit);

        return ResponseEntity.created(builder.path("/organisationalUnits/{organisationalUnitId}").build(save.getId())).build();
    }
}

