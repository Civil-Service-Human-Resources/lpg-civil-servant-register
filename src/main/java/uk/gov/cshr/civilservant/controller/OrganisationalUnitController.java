package uk.gov.cshr.civilservant.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.dto.OrganisationalUnitDto;
import uk.gov.cshr.civilservant.service.OrganisationalUnitService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RepositoryRestController
@RequestMapping("/organisationalUnits")
public class OrganisationalUnitController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrganisationalUnitController.class);

    private OrganisationalUnitService organisationalUnitService;

    public OrganisationalUnitController(OrganisationalUnitService organisationalUnitService) {
        this.organisationalUnitService = organisationalUnitService;
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

    @GetMapping("/allCodesMap")
    public ResponseEntity<Map<String, List<String>>> getAllCodes() {
        Map<String, List<String>> codeParentCodesMap = new HashMap<>();

        List<String> organisationalUnitCodes = organisationalUnitService.getOrganisationalUnitCodes();

        organisationalUnitCodes.forEach(s -> {
            List<String> parentCodes = organisationalUnitService.getOrganisationWithParents(s)
                    .stream()
                    .map(OrganisationalUnit::getCode)
                    .collect(Collectors.toList());
            codeParentCodesMap.put(s, parentCodes);
        });

        return ResponseEntity.ok(codeParentCodesMap);
    }
}

