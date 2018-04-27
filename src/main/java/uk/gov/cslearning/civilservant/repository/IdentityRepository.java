package uk.gov.cslearning.civilservant.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.civilservant.domain.Identity;

import java.util.Optional;

@Repository
public interface IdentityRepository extends CrudRepository<Identity, Long> {

    Optional<Identity> findByUid(String uid);
}
