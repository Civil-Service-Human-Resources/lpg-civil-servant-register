package uk.gov.cshr.civilservant.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import uk.gov.cshr.civilservant.domain.Profession;

@Repository
@RepositoryRestResource
public interface ProfessionRepository extends SelfReferencingEntityRepository<Profession> {

    @PreAuthorize("hasRole('PROFESSION_MANAGER')")
    @Override
    Profession save(Profession profession);
}
