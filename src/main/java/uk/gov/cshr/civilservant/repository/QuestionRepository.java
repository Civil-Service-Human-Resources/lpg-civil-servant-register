package uk.gov.cshr.civilservant.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import uk.gov.cshr.civilservant.domain.Question;

@Repository
@PreAuthorize("isAuthenticated()")
public interface QuestionRepository extends CrudRepository<Question, Long> {
  List<Question> findAll();
}
