package uk.gov.cshr.civilservant.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import uk.gov.cshr.civilservant.domain.Identity;

import java.util.Optional;

@Repository
@RepositoryRestResource(exported = false)
public interface IdentityRepository extends CrudRepository<Identity, Long> {

    Optional<Identity> findById(String id);
}
