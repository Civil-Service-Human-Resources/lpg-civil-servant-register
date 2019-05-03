package uk.gov.cshr.civilservant.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cshr.civilservant.domain.Question;

@Repository
public interface QuestionRepository extends CrudRepository<Question, Long> {

}
