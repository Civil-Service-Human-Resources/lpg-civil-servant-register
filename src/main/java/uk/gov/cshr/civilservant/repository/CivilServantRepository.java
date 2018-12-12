package uk.gov.cshr.civilservant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import uk.gov.cshr.civilservant.domain.*;

import java.util.List;
import java.util.Optional;


@Repository
@PreAuthorize("isAuthenticated()")
@RepositoryRestResource(excerptProjection = AllCivilServantDetails.class)
public interface CivilServantRepository extends JpaRepository<CivilServant, Long> {

    @RestResource(exported = false)
    @Query("select c from CivilServant c where c.identity.uid = ?#{principal}")
    Optional<CivilServant> findByPrincipal();

    @PreAuthorize("#civilServant.identity.uid eq principal || hasAuthority('INTERNAL')")
    CivilServant save(@Param("civilServant") CivilServant civilServant);

    @PostAuthorize("returnObject.isPresent() && returnObject.get().identity.uid eq principal")
    Optional<CivilServant> findById(@Param("id") Long id);

    @RestResource(path = "findByIdentity", rel = "findByIdentity")
    @Query("select c from CivilServant c where c.identity.uid = ?1")
    Optional<CivilServant> findByIdentity(@Param("uid") String uid);

    @RestResource(exported = false)
    Optional<CivilServant> findByIdentity(Identity identity);

    List<CivilServant> findAllByOrganisationalUnit(OrganisationalUnit organisationalUnit);

    List<CivilServant> findAllByProfession(Profession profession);
}
