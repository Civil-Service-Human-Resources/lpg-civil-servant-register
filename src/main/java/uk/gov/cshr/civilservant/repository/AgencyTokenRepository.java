package uk.gov.cshr.civilservant.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import uk.gov.cshr.civilservant.domain.AgencyToken;

@Repository
@RepositoryRestResource
public interface AgencyTokenRepository extends CrudRepository<AgencyToken, Long> {
}
