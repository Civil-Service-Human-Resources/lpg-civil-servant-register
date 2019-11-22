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

import java.util.ArrayList;
import java.util.List;

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

    @PutMapping
    public ResponseEntity updateSpaceAvailable(@RequestBody UpdateSpacesForAgencyTokenRequestDTO updateSpacesForAgencyTokenRequestDTO) {
        try {
            log.info("Updating agency token with parameters domain=" + updateSpacesForAgencyTokenRequestDTO.getDomain() +
                    " token=" + updateSpacesForAgencyTokenRequestDTO.getToken() +
                    " code=" + updateSpacesForAgencyTokenRequestDTO.getCode() +
                    " isRemoveUser=" + updateSpacesForAgencyTokenRequestDTO.isRemoveUser());

            List<OrganisationalUnit> PassedOrganisationalUnitList = new ArrayList<>();
            List<String> OrganisaitnalUnitCodeList = new ArrayList<>();
//            PassedOrganisationalUnitList = organisationalUnitService.getOrganisationWithParents(updateSpacesForAgencyTokenDTO.getCode());
//            for (OrganisationalUnit organisationalUnit: PassedOrganisationalUnitList)
//            {
//                OrganisaitnalUnitCodeList.add(organisationalUnit.getCode());
//            }

        } catch (TokenDoesNotExistException e) {
            log.warn("Token not found", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NotEnoughSpaceAvailableException e) {
            log.warn("Not enough space available for token", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("An error occurred", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
