package uk.gov.cshr.civilservant.controller;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.cshr.civilservant.service.AgencyTokenService;

@RepositoryRestController
@RequestMapping("/agencyTokens")
public class AgencyTokenController {

    private AgencyTokenService agencyTokenService;

    public AgencyTokenController(AgencyTokenService agencyTokenService) {
        this.agencyTokenService = agencyTokenService;
    }
}
