package uk.gov.cshr.civilservant.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cshr.civilservant.service.AgencyTokenService;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/agencyTokens")
public class AgencyTokenController {

    private AgencyTokenService agencyTokenService;

    public AgencyTokenController(AgencyTokenService agencyTokenService) {
        this.agencyTokenService = agencyTokenService;
    }

    @GetMapping(params = {"domain"})
    public ResponseEntity<Boolean> isDomainInAgency(@RequestParam String domain) {
        return ResponseEntity.ok(agencyTokenService.isDomainInAgency(domain));
    }

    @GetMapping(params = {"domain", "token", "code"})
    public ResponseEntity getAgencyTokenUidByDomainTokenAndOrganisationalUnit(@RequestParam String domain,
                                                                              @RequestParam String token,
                                                                              @RequestParam String code) {
        return agencyTokenService.getAgencyTokenByDomainTokenCodeAndOrg(domain, token, code)
                .map(agencyToken -> new ResponseEntity<>(agencyToken.getUid(), OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(params = {"uid"})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getAgencyTokenByUid(@RequestParam String uid) {
        return agencyTokenService.getAgencyTokenByUid(uid)
                .map(agencyToken -> new ResponseEntity<>(agencyToken, OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
