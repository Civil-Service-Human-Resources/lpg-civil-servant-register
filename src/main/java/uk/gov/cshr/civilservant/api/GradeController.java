package uk.gov.cshr.civilservant.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cshr.civilservant.domain.Grade;
import uk.gov.cshr.civilservant.domain.Organisation;
import uk.gov.cshr.civilservant.repository.OrganisationRepository;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.emptyList;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/grades")
public class GradeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GradeController.class);

    private OrganisationRepository organisationRepository;

    @Autowired
    public GradeController(OrganisationRepository organisationRepository) {
        checkArgument(organisationRepository != null);
        this.organisationRepository = organisationRepository;
    }

    @GetMapping
    public ResponseEntity<Results<Grade>> list(@RequestParam("organisation") String organisationCode) {
        LOGGER.debug("Listing grades for organisation {}", organisationCode);

        Optional<Organisation> result = organisationRepository.findByCode(organisationCode);
        return result
                .map(organisation -> new ResponseEntity<>(new Results<>(organisation.getGrades()), OK))
                .orElseGet(() -> new ResponseEntity<>(new Results<>(emptyList()), OK));
    }
}
