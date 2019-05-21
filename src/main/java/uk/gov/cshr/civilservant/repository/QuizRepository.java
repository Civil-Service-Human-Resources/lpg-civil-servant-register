package uk.gov.cshr.civilservant.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cshr.civilservant.domain.Quiz;

import java.util.Optional;

@Repository
public interface QuizRepository extends CrudRepository<Quiz, Long> {

    Optional<Quiz> findFirstByProfessionId(long id);

    void deleteAllByProfessionId(long id);
}
