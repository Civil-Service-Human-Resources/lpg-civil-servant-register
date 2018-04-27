package uk.gov.cshr.civilservant.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cshr.civilservant.domain.Department;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends CrudRepository<Department, Long> {

    Optional<Department> findByCode(String code);
}
