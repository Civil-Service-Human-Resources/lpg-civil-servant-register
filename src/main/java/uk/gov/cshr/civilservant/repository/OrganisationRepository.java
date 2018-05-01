package uk.gov.cshr.civilservant.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cshr.civilservant.domain.Organisation;

@Repository
public interface OrganisationRepository extends CrudRepository<Organisation, Long> {

    Iterable<Organisation> findByNameStartsWith(String query);
}
