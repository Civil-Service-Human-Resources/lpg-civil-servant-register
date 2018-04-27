package uk.gov.cshr.civilservant.api;

import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cshr.civilservant.domain.Department;
import uk.gov.cshr.civilservant.repository.DepartmentRepository;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentController.class);

    private DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentController(DepartmentRepository departmentRepository) {
        checkArgument(departmentRepository != null);
        this.departmentRepository = departmentRepository;
    }

    @GetMapping
    public ResponseEntity<Results<Department>> list() {
        LOGGER.debug("Listing departments");

        Iterable<Department> results = departmentRepository.findAll();
        List<Department> departments = new ArrayList<>();

        Iterables.addAll(departments, results);

        return new ResponseEntity<>(new Results<>(departments), OK);
    }
}
