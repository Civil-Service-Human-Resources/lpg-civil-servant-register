package uk.gov.cshr.civilservant.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;
import uk.gov.cshr.civilservant.domain.Organisation;

import java.util.Optional;

@Repository
@RepositoryRestResource
public interface OrganisationRepository extends CrudRepository<Organisation, Long> {

    @RestResource(path = "name", rel = "searchName")
    Iterable<Organisation> findByNameStartsWithIgnoringCase(@Param("query") String query);

    @RestResource(path = "findByDepartmentCode", rel = "findByDepartmentCode")
    @Query("SELECT o FROM Organisation o WHERE o.department.code = ?1")
    Optional<Organisation> findByDepartmentCode(@Param("departmentCode") String code);
}
