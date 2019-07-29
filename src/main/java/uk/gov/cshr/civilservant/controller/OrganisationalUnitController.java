package uk.gov.cshr.civilservant.controller;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.dto.OrganisationalUnitDto;
import uk.gov.cshr.civilservant.service.AgencyTokenService;
import uk.gov.cshr.civilservant.service.OrganisationalUnitService;

import java.util.List;

@RepositoryRestController
@RequestMapping("/organisationalUnits")
public class OrganisationalUnitController {

    private OrganisationalUnitService organisationalUnitService;
    private AgencyTokenService agencyTokenService;

    public OrganisationalUnitController(OrganisationalUnitService organisationalUnitService, AgencyTokenService agencyTokenService) {
        this.organisationalUnitService = organisationalUnitService;
        this.agencyTokenService = agencyTokenService;
    }


    @GetMapping("/tree")
    @Cacheable("organisationalUnitsTree")
    public ResponseEntity<List<OrganisationalUnit>> listOrganisationalUnitsAsTreeStructure() {
        List<OrganisationalUnit> organisationalUnits = organisationalUnitService.getParents();

        return ResponseEntity.ok(organisationalUnits);
    }

    @GetMapping("/flat")
    @Cacheable("organisationalUnitsFlat")
    public ResponseEntity<List<OrganisationalUnitDto>> listOrganisationalUnitsAsFlatStructure() {
        List<OrganisationalUnitDto> organisationalUnitsMap = organisationalUnitService.getListSortedByValue();

        return ResponseEntity.ok(organisationalUnitsMap);
    }

    @GetMapping("/parent/{code}")
    public ResponseEntity<List<OrganisationalUnit>> getOrganisationWithParents(@PathVariable String code) {
        return ResponseEntity.ok(organisationalUnitService.getOrganisationWithParents(code));
    }

    @PostMapping("/{id}/agencyToken")
    public ResponseEntity<OrganisationalUnit> saveAgencyToken(@PathVariable Long id, @RequestBody AgencyToken agencyToken) {
        return ResponseEntity.ok(organisationalUnitService.save(id, agencyToken));

    }

    @PatchMapping("/{id}/agencyToken/{tokenId}")
    public ResponseEntity<OrganisationalUnit> updateAgencyToken(@PathVariable Long id, @RequestBody AgencyToken agencyToken) {
        return ResponseEntity.ok(organisationalUnitService.save(id, agencyToken));
    }

    @GetMapping("/normalised")
    public ResponseEntity<List<OrganisationalUnit>> getOrganisationNormalised() {
        return ResponseEntity.ok(organisationalUnitService.getOrganisationsNormalised());
    }
}
