package uk.gov.cshr.civilservant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;

import java.util.List;

@Repository
@RepositoryRestResource
public interface OrganisationalUnitRepository extends JpaRepository<OrganisationalUnit, Long> {
    OrganisationalUnit findByCode(@Param("code") String code);

    List<OrganisationalUnit> findAllByOrderByNameAsc();
}
