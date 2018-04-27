package uk.gov.cslearning.civilservant.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.civilservant.domain.Organisation;

import java.util.Optional;

@Repository
public interface OrganisationRepository extends CrudRepository<Organisation, Long> {

    Optional<Organisation> findByCode(String code);
}
