package uk.gov.cshr.civilservant.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import uk.gov.cshr.civilservant.domain.Quiz;
import uk.gov.cshr.civilservant.domain.Status;

@Repository
@PreAuthorize("isAuthenticated()")
public interface QuizRepository extends JpaRepository<Quiz, Long> {

  Optional<Quiz> findFirstByProfessionIdAndStatusIsNot(long id, Status status);

  Optional<Quiz> findFirstByProfessionIdAndStatusIs(long id, Status status);
}