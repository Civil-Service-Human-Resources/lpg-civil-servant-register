package uk.gov.cshr.civilservant.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.dto.UpdateSpacesForAgencyTokenRequestDTO;
import uk.gov.cshr.civilservant.exception.NotEnoughSpaceAvailableException;
import uk.gov.cshr.civilservant.exception.TokenDoesNotExistException;
import uk.gov.cshr.civilservant.service.AgencyTokenService;
import uk.gov.cshr.civilservant.service.OrganisationalUnitService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/agencyTokens")
public class AgencyTokenController {

    private AgencyTokenService agencyTokenService;
    private OrganisationalUnitService organisationalUnitService;

    public AgencyTokenController(AgencyTokenService agencyTokenService, OrganisationalUnitService organisationalUnitService) {
        this.agencyTokenService = agencyTokenService;
        this.organisationalUnitService = organisationalUnitService;
    }

    @GetMapping
    public ResponseEntity getAgencyTokens() {
        return ResponseEntity.ok(agencyTokenService.getAllAgencyTokens());
    }

    @GetMapping(params = {"domain"})
    public ResponseEntity getAgencyTokensByDomain(@RequestParam String domain) {
        return ResponseEntity.ok(agencyTokenService.getAllAgencyTokensByDomain(domain));
    }

    @GetMapping(params = {"domain", "token"})
    public ResponseEntity getAgencyTokensByDomainAndToken(@RequestParam String domain, @RequestParam String token) {
        return agencyTokenService.getAgencyTokenByDomainAndToken(domain, token)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(params = {"domain", "code"})
    public ResponseEntity getAgencyTokensByDomainAndOrganisationalUnit(@RequestParam String domain, @RequestParam String code) {
        return agencyTokenService.getAgencyTokenByDomainAndOrganisation(domain, code)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(params = {"domain", "token", "code"})
    public ResponseEntity getAgencyTokensByDomainTokenAndOrganisationalUnit(@RequestParam String domain, @RequestParam String token, @RequestParam String code) {
        return agencyTokenService.getAgencyTokenByDomainTokenAndOrganisation(domain, token, code)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
