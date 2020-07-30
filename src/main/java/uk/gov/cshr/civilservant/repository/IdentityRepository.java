package uk.gov.cshr.civilservant.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import uk.gov.cshr.civilservant.domain.Identity;

@Repository
@RepositoryRestResource(exported = false)
public interface IdentityRepository extends CrudRepository<Identity, Long> {

  Optional<Identity> findByUid(String uid);
}
