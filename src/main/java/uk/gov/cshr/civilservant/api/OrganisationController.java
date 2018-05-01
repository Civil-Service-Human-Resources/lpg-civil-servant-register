package uk.gov.cshr.civilservant.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.cshr.civilservant.domain.Organisation;
import uk.gov.cshr.civilservant.repository.OrganisationRepository;

import java.util.Optional;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/organisations")
public class OrganisationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganisationController.class);

    private OrganisationRepository organisationRepository;

    @Autowired
    public OrganisationController(OrganisationRepository organisationRepository) {
        checkArgument(organisationRepository != null);
        this.organisationRepository = organisationRepository;
    }

    @GetMapping
    public ResponseEntity<Results<OrganisationResource>> list(@RequestParam("query") String query) {
        LOGGER.debug("Listing organisations for query {}", query);

        Iterable<Organisation> results = organisationRepository.findByNameStartsWith(query);
        return new ResponseEntity<>(new Results<>(StreamSupport.stream(results.spliterator(), false)
                .map(OrganisationResource::new)
                .collect(toList())), OK);
    }

    @GetMapping("/{organisationId")
    public ResponseEntity<OrganisationResource> get(@PathVariable Long organisationId) {
        LOGGER.debug("Getting organisation with id {}", organisationId);

        Optional<Organisation> result = organisationRepository.findById(organisationId);
        return result
                .map(organisation -> new ResponseEntity<>(new OrganisationResource(organisation, false), OK))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }

    @GetMapping("/{organisationId/grades")
    public ResponseEntity<Results<GradeResource>> listGrades(@PathVariable Long organisationId) {
        LOGGER.debug("Listing grades for organisation with id {}", organisationId);

        Optional<Organisation> result = organisationRepository.findById(organisationId);
        return result
                .map(organisation -> new ResponseEntity<>(new Results<>(organisation.getGrades().stream().map(GradeResource::new).collect(toList())), OK))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }
}
