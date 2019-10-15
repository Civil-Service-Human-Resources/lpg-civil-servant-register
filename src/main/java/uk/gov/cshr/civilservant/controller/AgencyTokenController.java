package uk.gov.cshr.civilservant.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cshr.civilservant.dto.AgencyTokenDTO;
import uk.gov.cshr.civilservant.exception.NotEnoughSpaceAvailableException;
import uk.gov.cshr.civilservant.exception.TokenDoesNotExistException;
import uk.gov.cshr.civilservant.service.AgencyTokenService;

@Slf4j
@RestController
@RequestMapping("/agencyTokens")
public class AgencyTokenController {

    private AgencyTokenService agencyTokenService;

    public AgencyTokenController(AgencyTokenService agencyTokenService) {
        this.agencyTokenService = agencyTokenService;
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

    @GetMapping(params = {"domain", "token", "code"})
    public ResponseEntity getAgencyTokensByDomainTokenAndOrganisationalUnit(@RequestParam String domain, @RequestParam String token, @RequestParam String code) {
        return agencyTokenService.getAgencyTokenByDomainTokenAndOrganisation(domain, token, code)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity getSpaceAvailable(@RequestBody AgencyTokenDTO agencyTokenDTO) {
        try {
            log.info("Updating agency token with parameters domain=" + agencyTokenDTO.getDomain() +
                    " token=" + agencyTokenDTO.getToken() +
                    " code=" + agencyTokenDTO.getCode());
            agencyTokenService.updateAgencyTokenSpacesAvailable(agencyTokenDTO.getDomain(), agencyTokenDTO.getToken(), agencyTokenDTO.getCode());
        } catch (TokenDoesNotExistException e) {
            log.warn("Token not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NotEnoughSpaceAvailableException e) {
            log.warn("Not enough space availbale for token");
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("An error occurred");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
