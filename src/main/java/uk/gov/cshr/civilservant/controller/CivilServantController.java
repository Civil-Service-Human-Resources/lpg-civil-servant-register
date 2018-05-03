package uk.gov.cshr.civilservant.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

@RepositoryRestController
@RequestMapping("/civilServants")
@PreAuthorize("isAuthenticated()")
public class CivilServantController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CivilServantController.class);

    private CivilServantRepository civilServantRepository;

    private RepositoryEntityLinks repositoryEntityLinks;

    @Autowired
    public CivilServantController(CivilServantRepository civilServantRepository,
                                  RepositoryEntityLinks repositoryEntityLinks) {
        checkArgument(civilServantRepository != null);
        checkArgument(repositoryEntityLinks != null);
        this.civilServantRepository = civilServantRepository;
        this.repositoryEntityLinks = repositoryEntityLinks;
    }

    @GetMapping("/me")
    public String get() {
        LOGGER.debug("Getting civil servant details for logged in user");
        Optional<CivilServant> optionalCivilServant = civilServantRepository.findByPrincipal();
        return optionalCivilServant
                .map(civilServant -> "redirect:" + repositoryEntityLinks.linkToSingleResource(CivilServant.class, civilServant.getId()).expand().getHref())
                .orElse(null);
    }
}
