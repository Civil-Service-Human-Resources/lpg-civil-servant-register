package uk.gov.cshr.civilservant.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.Identity;

import java.util.Optional;

@Repository
@RepositoryRestResource(exported = false)
public interface InternalCivilServantRepository extends org.springframework.data.repository.Repository<CivilServant, Long> {

    Optional<CivilServant> findByIdentity(Identity identity);

    CivilServant save(CivilServant civilServant);
}
