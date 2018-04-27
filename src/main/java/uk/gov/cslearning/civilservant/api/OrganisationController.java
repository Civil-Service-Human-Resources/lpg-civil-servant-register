package uk.gov.cslearning.civilservant.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.civilservant.domain.Department;
import uk.gov.cslearning.civilservant.domain.Organisation;
import uk.gov.cslearning.civilservant.repository.DepartmentRepository;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.emptyList;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/organisations")
public class OrganisationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganisationController.class);

    private DepartmentRepository departmentRepository;

    @Autowired
    public OrganisationController(DepartmentRepository departmentRepository) {
        checkArgument(departmentRepository != null);
        this.departmentRepository = departmentRepository;
    }

    @GetMapping
    public ResponseEntity<Results<Organisation>> list(@RequestParam("department") String departmentCode) {
        LOGGER.debug("Listing organisations for department {}", departmentCode);

        Optional<Department> result = departmentRepository.findByCode(departmentCode);
        return result
                .map(department -> new ResponseEntity<>(new Results<>(department.getOrganisations()), OK))
                .orElseGet(() -> new ResponseEntity<>(new Results<>(emptyList()), OK));
    }
}
