package uk.gov.cshr.civilservant.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cshr.civilservant.domain.Quiz;

@Repository
public interface QuizRepository extends CrudRepository<Quiz, Long> {
    Quiz findFirstByProfessionId(long id);
}
