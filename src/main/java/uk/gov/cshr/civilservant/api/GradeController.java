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
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/grades")
public class GradeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GradeController.class);

    private GradeRepository gradeRepository;

    private OrganisationRepository organisationRepository;

    @Autowired
    public GradeController(GradeRepository gradeRepository, OrganisationRepository organisationRepository) {
        checkArgument(gradeRepository != null);
        checkArgument(organisationRepository != null);
        this.gradeRepository = gradeRepository;
        this.organisationRepository = organisationRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<Results<GradeResource>> list(@RequestParam("organisation") Long organisationId) {
        LOGGER.debug("Listing grades for organisation with id {}", organisationId);

        Optional<Organisation> result = organisationRepository.findById(organisationId);

        return result
                .map(organisation -> {
                    Iterable<Grade> grades = organisation.getGrades();
                    if (Iterables.isEmpty(grades)) {
                        grades = gradeRepository.findByOrganisationIsNull();
                    }
                    return ResponseEntity.ok(new Results<>(StreamSupport.stream(grades.spliterator(), false).map(GradeResource::new).collect(toList())));
                })
                .orElseGet(() -> ResponseEntity.ok(new Results<>(emptyList())));
    }

    @GetMapping("/{gradeId}")
    @Transactional(readOnly = true)
    public ResponseEntity<GradeResource> get(@PathVariable Long gradeId) {
        LOGGER.debug("Getting grade with id {}", gradeId);

        Optional<Grade> result = gradeRepository.findById(gradeId);
        return result
                .map(grade -> ResponseEntity.ok(new GradeResource(grade)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
