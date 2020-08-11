package uk.gov.cshr.civilservant.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import uk.gov.cshr.civilservant.domain.QuizResult;
import uk.gov.cshr.civilservant.dto.QuizResultDto;
import uk.gov.cshr.civilservant.dto.QuizResultSummaryDto;

@Repository
@PreAuthorize("isAuthenticated()")
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
  @Query(
      value =
          "SELECT new uk.gov.cshr.civilservant.dto.QuizResultDto("
              + "result.id,"
              + "result.staffId,"
              + "result.quizId,"
              + "result.quizName,"
              + "result.professionId,"
              + "result.organisationId,"
              + "result.type,"
              + "result.result,"
              + "result.correctAnswers,"
              + "result.numberOfQuestions,"
              + "result.score,"
              + "result.completedOn)"
              + " FROM QuizResult result"
              + " WHERE result.staffId = :staffId"
              + " ORDER BY result.completedOn DESC ")
  List<QuizResultDto> findQuizResultByStaffId(String staffId);

  @Query(
      value =
          "SELECT new uk.gov.cshr.civilservant.dto.QuizResultSummaryDto("
              + "result.professionId, "
              + "COUNT (result), "
              + "AVG (result.score))"
              + "FROM QuizResult result "
              + "INNER JOIN Quiz quiz "
              + "ON result.quizId = quiz.id "
              + "WHERE result.professionId = :professionId "
              + "AND quiz.status NOT LIKE 'INACTIVE' "
              + "GROUP BY result.professionId")
  QuizResultSummaryDto findByProfessionId(long professionId);

  Optional<QuizResult> findQuizResultByIdAndStaffId(long id, String staffId);

  @Query(
      value =
          "SELECT new uk.gov.cshr.civilservant.dto.QuizResultSummaryDto("
              + "result.professionId, "
              + "COUNT (result), "
              + "AVG (result.score))"
              + "FROM QuizResult result "
              + "INNER JOIN Quiz quiz "
              + "ON result.quizId = quiz.id "
              + "WHERE result.organisationId = :organisationId "
              + "AND quiz.status NOT LIKE 'INACTIVE' "
              + "GROUP BY result.professionId")
  List<QuizResultSummaryDto> findByOrOrganisationIdOrderByQuizNameAsc(long organisationId);

  @Query(
      value =
          "SELECT new uk.gov.cshr.civilservant.dto.QuizResultSummaryDto("
              + "result.professionId, "
              + "COUNT (result), "
              + "AVG (result.score))"
              + "FROM QuizResult result "
              + "INNER JOIN Quiz quiz "
              + "ON result.quizId = quiz.id "
              + "WHERE quiz.status NOT LIKE 'INACTIVE' "
              + "GROUP BY result.professionId")
  List<QuizResultSummaryDto> findAllResults();

  List<QuizResult> findAllByCompletedOnBetween(LocalDateTime from, LocalDateTime to);

  List<QuizResult> findAllByOrganisationIdAndCompletedOnBetween(
      long organisationId, LocalDateTime from, LocalDateTime to);

  List<QuizResult> findAllByOrganisationIdAndProfessionIdAndCompletedOnBetween(
      long organisationId, long professionId, LocalDateTime from, LocalDateTime to);

  List<QuizResult> findAllByProfessionIdAndCompletedOnBetween(
      long professionId, LocalDateTime from, LocalDateTime to);
}
