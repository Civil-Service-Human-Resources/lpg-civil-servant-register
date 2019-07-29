package uk.gov.cshr.civilservant.repository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;

import java.util.List;
import java.util.Optional;

@Repository
@RepositoryRestResource
public interface OrganisationalUnitRepository extends SelfReferencingEntityRepository<OrganisationalUnit> {

    Optional<OrganisationalUnit> findByCode(@Param("code") String code);

    @Query(value = "select new uk.gov.cshr.civilservant.domain.OrganisationalUnit(o.name, o.code, o.abbreviation) " +
            "from OrganisationalUnit o")
    List<OrganisationalUnit> findAllNormalised();

//    @Override
//    @CacheEvict(value = {"organisationalUnitsTree", "organisationalUnitsFlat"}, allEntries = true)
//    default <S extends OrganisationalUnit> S save(S entity){
//        S saved = save(entity);
//        return saved;
//    }

    @Override
    @CacheEvict(value = {"organisationalUnitsTree", "organisationalUnitsFlat"}, allEntries = true)
    <S extends OrganisationalUnit> S save(S entity);
}
