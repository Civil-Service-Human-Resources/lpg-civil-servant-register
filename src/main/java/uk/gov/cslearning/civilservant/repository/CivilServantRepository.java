package uk.gov.cslearning.civilservant.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.civilservant.domain.CivilServant;

@Repository
public interface CivilServantRepository extends CrudRepository<CivilServant, Long> {

}
