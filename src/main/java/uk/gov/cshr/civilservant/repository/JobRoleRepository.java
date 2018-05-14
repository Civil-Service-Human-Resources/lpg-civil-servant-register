package uk.gov.cshr.civilservant.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;
import uk.gov.cshr.civilservant.domain.JobRole;
import uk.gov.cshr.civilservant.domain.Profession;

@Repository
@RepositoryRestResource
public interface JobRoleRepository extends CrudRepository<JobRole, Long> {

    @RestResource(path = "profession")
    Iterable<JobRole> findByProfession(@Param("profession") Profession profession);

    @RestResource(path = "parent")
    Iterable<JobRole> findByParent(@Param("parent") JobRole parent);
}
