package uk.gov.cshr.civilservant.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.dto.AgencyTokenDTO;
import uk.gov.cshr.civilservant.dto.OrganisationalUnitDto;
import uk.gov.cshr.civilservant.dto.factory.AgencyTokenFactory;
import uk.gov.cshr.civilservant.dto.factory.AgencyTokenFactory;
import uk.gov.cshr.civilservant.dto.factory.OrganisationalUnitDtoFactory;
import uk.gov.cshr.civilservant.service.OrganisationalUnitService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/organisationalUnits")
public class OrganisationalUnitController {

    private OrganisationalUnitService organisationalUnitService;

    private OrganisationalUnitDtoFactory organisationalUnitDtoFactory;

    private AgencyTokenFactory agencyTokenFactory;

    public OrganisationalUnitController(OrganisationalUnitService organisationalUnitService, OrganisationalUnitDtoFactory organisationalUnitDtoFactory, AgencyTokenFactory agencyTokenFactory) {
        this.organisationalUnitService = organisationalUnitService;
        this.agencyTokenService = agencyTokenService;
    }

    @GetMapping("/tree")
    @Cacheable("organisationalUnitsTree")
    public ResponseEntity<List<OrganisationalUnit>> listOrganisationalUnitsAsTreeStructure() {
        log.info("Getting org tree");
        List<OrganisationalUnit> organisationalUnits = organisationalUnitService.getParents();

        return ResponseEntity.ok(organisationalUnits);
    }

    @GetMapping("/flat")
    @Cacheable("organisationalUnitsFlat")
    public ResponseEntity<List<OrganisationalUnitDto>> listOrganisationalUnitsAsFlatStructure() {
        log.info("Getting org flat");
        List<OrganisationalUnitDto> organisationalUnitsMap = organisationalUnitService.getListSortedByValue();

        return ResponseEntity.ok(organisationalUnitsMap);
    }

    @GetMapping("/flat/{domain}/")
    public ResponseEntity<List<OrganisationalUnitDto>> listOrganisationalUnitsAsFlatStructureFilteredByDomainAndCode(@PathVariable String domain) {
        log.info("Getting org flat, filtered by domain");

        List<OrganisationalUnit> organisationalUnits = organisationalUnitService.getOrganisationsForDomain(domain);
        if(organisationalUnits.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<OrganisationalUnitDto> dtos = organisationalUnits.stream()
                .map(ou -> organisationalUnitDtoFactory.create(ou))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/children/{code}")
    public ResponseEntity<List<OrganisationalUnit>> getOrganisationWithChildren(@PathVariable String code) {
        log.info("Getting org for current family only, current and any children");
        return ResponseEntity.ok(organisationalUnitService.getOrganisationWithChildren(code));
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
    public ResponseEntity saveAgencyToken(@PathVariable Long organisationalUnitId, @RequestBody AgencyToken agencyToken) {
        return organisationalUnitService.getOrganisationalUnit(organisationalUnitId).map(organisationalUnit -> {
            organisationalUnitService.setAgencyToken(organisationalUnit, agencyToken);
            return ResponseEntity.created(builder.path("/organisationalUnits/{organisationalUnitId}/agencyToken").build(organisationalUnit.getId())).build();
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @GetMapping("/{organisationalUnitId}/agencyToken")
    public ResponseEntity getAgencyToken(@PathVariable Long organisationalUnitId) {
        return organisationalUnitService.getOrganisationalUnit(organisationalUnitId)
                .map(organisationalUnit -> ResponseEntity.ok(organisationalUnit.getAgencyToken()))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/{organisationalUnitId}/agencyToken")
    public ResponseEntity updateAgencyToken(@PathVariable Long organisationalUnitId, @RequestBody AgencyToken agencyToken) {
        return organisationalUnitService.getOrganisationalUnit(organisationalUnitId).map(organisationalUnit -> {
            organisationalUnitService.updateAgencyToken(organisationalUnit, agencyToken);
            return ResponseEntity.ok(agencyToken);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
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

    @PostMapping("/{organisationalUnitId}/agencyToken")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity saveAgencyToken(@PathVariable Long organisationalUnitId, @Valid @RequestBody AgencyToken agencyToken, BindingResult bindingResult, UriComponentsBuilder builder) {
        if(bindingResult.hasErrors()){
            new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

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
    public ResponseEntity updateAgencyToken(@PathVariable Long organisationalUnitId, @Valid @RequestBody AgencyTokenDTO agencyTokenDTO) {
        AgencyToken agencyToken = agencyTokenFactory.buildAgencyTokenFromAgencyTokenDTO(agencyTokenDTO, false);
        return organisationalUnitService.getOrganisationalUnit(organisationalUnitId).map(organisationalUnit -> {
            organisationalUnitService.updateAgencyToken(organisationalUnit, agencyToken);
            return ResponseEntity.ok(agencyToken);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.CONFLICT));
    }

    @DeleteMapping("/{organisationalUnitId}/agencyToken")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity deleteAgencyToken(@PathVariable Long organisationalUnitId) {
        return organisationalUnitService.getOrganisationalUnit(organisationalUnitId).map(organisationalUnit -> {
            organisationalUnitService.deleteAgencyToken(organisationalUnit);
            return ResponseEntity.ok(organisationalUnit);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    /*@InitBinder("AgencyTokenDTO")
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(new AgencyTokenDTOValidator());
    }*/
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}
