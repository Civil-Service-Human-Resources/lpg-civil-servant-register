package uk.gov.cshr.civilservant.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cshr.civilservant.domain.Organisation;

import java.util.Optional;

@Repository
public interface OrganisationRepository extends CrudRepository<Organisation, Long> {

    Optional<Organisation> findByCode(String code);
}
