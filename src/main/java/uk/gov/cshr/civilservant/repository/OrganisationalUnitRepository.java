package uk.gov.cshr.civilservant.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;

@Repository
@RepositoryRestResource
public interface OrganisationalUnitRepository  extends CrudRepository<OrganisationalUnit, Long> {
    OrganisationalUnit findByCode(@Param("code") String code);
}
