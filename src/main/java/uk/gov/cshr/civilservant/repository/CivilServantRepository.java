package uk.gov.cshr.civilservant.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import uk.gov.cshr.civilservant.domain.CivilServant;

import java.util.Optional;

@Repository
@RepositoryRestResource
@PreAuthorize("isAuthenticated()")
public interface CivilServantRepository extends org.springframework.data.repository.Repository<CivilServant, Long> {

    @RestResource(exported = false)
    @Query("select c from CivilServant c where c.identity.uid = ?#{principal}")
    Optional<CivilServant> findByPrincipal();

    @PreAuthorize("#civilServant.identity.uid eq principal")
    CivilServant save(@Param("civilServant") CivilServant civilServant);

    @PostAuthorize("returnObject.isPresent() && returnObject.get().identity.uid eq principal")
    Optional<CivilServant> findById(Long id);
}
