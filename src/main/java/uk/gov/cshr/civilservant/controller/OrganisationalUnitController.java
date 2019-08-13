package uk.gov.cshr.civilservant.controller;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cshr.civilservant.domain.AgencyToken;
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

    private OrganisationalUnitService organisationalUnitService;

    public OrganisationalUnitController(OrganisationalUnitService organisationalUnitService) {
        this.organisationalUnitService = organisationalUnitService;
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

    @GetMapping("/normalised")
    public ResponseEntity<List<OrganisationalUnit>> getOrganisationNormalised() {
        return ResponseEntity.ok(organisationalUnitService.getOrganisationsNormalised());
    }

    @PostMapping("/{organisationalUnitId}/agencyToken")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity saveAgencyToken(@PathVariable Long organisationalUnitId, @RequestBody AgencyToken agencyToken, UriComponentsBuilder builder) {
        return organisationalUnitService.getOrganisationalUnit(organisationalUnitId).map(organisationalUnit -> {
            organisationalUnitService.setAgencyToken(organisationalUnit, agencyToken);
            return ResponseEntity.created(builder.path("/organisationalUnits/{organisationalUnitId}/agencyToken").build(organisationalUnit.getId())).build();
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @GetMapping("/{organisationalUnitId}/agencyToken")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getAgencyToken(@PathVariable Long organisationalUnitId) {
        return organisationalUnitService.getOrganisationalUnit(organisationalUnitId)
                .map(organisationalUnit -> ResponseEntity.ok(organisationalUnit.getAgencyToken()))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/{organisationalUnitId}/agencyToken")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity updateAgencyToken(@PathVariable Long organisationalUnitId, @RequestBody AgencyToken agencyToken) {
        return organisationalUnitService.getOrganisationalUnit(organisationalUnitId).map(organisationalUnit -> {
            organisationalUnitService.updateAgencyToken(organisationalUnit, agencyToken);
            return ResponseEntity.ok(agencyToken);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("/{organisationalUnitId}/agencyToken")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity deleteAgencyToken(@PathVariable Long organisationalUnitId) {
        return organisationalUnitService.getOrganisationalUnit(organisationalUnitId).map(organisationalUnit -> {
            organisationalUnitService.deleteAgencyToken(organisationalUnit);
            return ResponseEntity.ok(organisationalUnit);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
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
