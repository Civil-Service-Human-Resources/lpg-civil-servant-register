package uk.gov.cshr.civilservant.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.Identity;

import java.util.Optional;

@Repository
public interface CivilServantRepository extends CrudRepository<CivilServant, Long> {

    Optional<CivilServant> findByIdentity(Identity identity);
}
