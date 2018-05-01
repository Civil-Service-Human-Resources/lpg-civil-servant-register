package uk.gov.cshr.civilservant.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cshr.civilservant.domain.Grade;
import uk.gov.cshr.civilservant.repository.GradeRepository;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/grades")
public class GradeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GradeController.class);

    private GradeRepository gradeRepository;

    @Autowired
    public GradeController(GradeRepository gradeRepository) {
        checkArgument(gradeRepository != null);
        this.gradeRepository = gradeRepository;
    }

    @GetMapping("/{gradeId")
    public ResponseEntity<GradeResource> get(@PathVariable Long gradeId) {
        LOGGER.debug("Getting grade with id {}", gradeId);

        Optional<Grade> result = gradeRepository.findById(gradeId);
        return result
                .map(organisation -> new ResponseEntity<>(new GradeResource(organisation), OK))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }
}
