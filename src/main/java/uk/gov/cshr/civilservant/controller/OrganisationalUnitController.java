package uk.gov.cshr.civilservant.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.dto.OrganisationalUnitDto;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;
import uk.gov.cshr.civilservant.service.OrganisationalUnitService;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;


@RepositoryRestController
@RequestMapping("/organisationalUnits")
public class OrganisationalUnitController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrganisationalUnitController.class);

    private OrganisationalUnitService organisationalUnitService;

    private CivilServantRepository civilServantRepository;

    public OrganisationalUnitController(OrganisationalUnitService organisationalUnitService,
                                        CivilServantRepository civilServantRepository) {
        this.organisationalUnitService = organisationalUnitService;
        this.civilServantRepository = civilServantRepository;
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

    @PostMapping("/addOrganisationReportingPermission/{uid}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity addOrganisationReportingPermission(@PathVariable String uid, @Valid @RequestBody ArrayList<String> organisationIds) {
        saveOrUpdate(uid, organisationIds, "add");
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/updateOrganisationReportingPermission/{uid}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity updateOrganisationReportingPermission(@PathVariable String uid, @Valid @RequestBody ArrayList<String> organisationIds) {
        saveOrUpdate(uid, organisationIds, "update");
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/deleteOrganisationReportingPermission/{uid}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity deleteOrganisationReportingPermission(@PathVariable String uid) {
        Optional<CivilServant> civilServant = civilServantRepository.findByIdentity(uid);
        organisationalUnitService.deleteOrganisationReportingPermission(civilServant.get().getId());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private void saveOrUpdate(String uid, ArrayList<String> organisationIds, String addOrUpdate) {
        Optional<CivilServant> civilServant = civilServantRepository.findByIdentity(uid);
        List<String> listOrganisationCodes = organisationalUnitService.getOrganisationalUnitCodesForIds(organisationIds);
        List<Long> organisationIdWithChildrenIds = organisationalUnitService.getOrganisationIdWithChildrenIds(listOrganisationCodes);
        if(addOrUpdate.equalsIgnoreCase("add")){
            organisationalUnitService.addOrganisationReportingPermission(civilServant.get().getId(), organisationIdWithChildrenIds);
        } else {
            organisationalUnitService.updateOrganisationReportingPermission(civilServant.get().getId(), organisationIdWithChildrenIds);
        }
    }
}

