package uk.gov.cshr.civilservant.api;

import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import uk.gov.cshr.civilservant.domain.Grade;
import uk.gov.cshr.civilservant.domain.Organisation;
import uk.gov.cshr.civilservant.repository.GradeRepository;
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

    private GradeRepository gradeRepository;

    private OrganisationRepository organisationRepository;

    @Autowired
    public OrganisationController(GradeRepository gradeRepository, OrganisationRepository organisationRepository) {
        checkArgument(gradeRepository != null);
        checkArgument(organisationRepository != null);
        this.gradeRepository = gradeRepository;
        this.organisationRepository = organisationRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<Results<OrganisationResource>> list(@RequestParam("query") String query) {
        LOGGER.debug("Listing organisations for query {}", query);

        Iterable<Organisation> results = organisationRepository.findByNameStartsWithIgnoringCase(query);
        return new ResponseEntity<>(new Results<>(StreamSupport.stream(results.spliterator(), false)
                .map(OrganisationResource::new)
                .collect(toList())), OK);
    }

    @GetMapping("/{organisationId}")
    @Transactional(readOnly = true)
    public ResponseEntity<OrganisationResource> get(@PathVariable Long organisationId) {
        LOGGER.debug("Getting organisation with id {}", organisationId);

        Optional<Organisation> result = organisationRepository.findById(organisationId);
        return result
                .map(organisation -> new ResponseEntity<>(new OrganisationResource(organisation, false), OK))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }

    @GetMapping("/{organisationId}/grades")
    @Transactional(readOnly = true)
    public ResponseEntity<Results<GradeResource>> listGrades(@PathVariable Long organisationId) {
        LOGGER.debug("Listing grades for organisation with id {}", organisationId);

        Optional<Organisation> result = organisationRepository.findById(organisationId);

        return result
                .map(organisation -> {
                    Iterable<Grade> grades = organisation.getGrades();
                    if (Iterables.isEmpty(grades)) {
                        grades = gradeRepository.findByDefaultTrue();
                    }
                    return ResponseEntity.ok(new Results<>(StreamSupport.stream(grades.spliterator(), false).map(GradeResource::new).collect(toList())));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
