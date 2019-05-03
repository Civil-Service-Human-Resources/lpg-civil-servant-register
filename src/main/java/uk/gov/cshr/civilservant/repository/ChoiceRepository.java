package uk.gov.cshr.civilservant.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cshr.civilservant.domain.Choice;

@Repository
public interface ChoiceRepository extends CrudRepository<Choice, Long> {

}
