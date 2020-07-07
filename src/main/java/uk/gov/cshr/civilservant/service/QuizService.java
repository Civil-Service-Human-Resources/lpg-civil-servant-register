package uk.gov.cshr.civilservant.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.Profession;
import uk.gov.cshr.civilservant.domain.Question;
import uk.gov.cshr.civilservant.domain.Quiz;
import uk.gov.cshr.civilservant.domain.QuizResult;
import uk.gov.cshr.civilservant.domain.QuizType;
import uk.gov.cshr.civilservant.domain.Status;
import uk.gov.cshr.civilservant.domain.SubmittedAnswer;
import uk.gov.cshr.civilservant.dto.AnswerDto;
import uk.gov.cshr.civilservant.dto.QuizDataTableDto;
import uk.gov.cshr.civilservant.dto.QuizDto;
import uk.gov.cshr.civilservant.dto.QuizHistoryDto;
import uk.gov.cshr.civilservant.dto.QuizResultDto;
import uk.gov.cshr.civilservant.dto.QuizResultSummaryDto;
import uk.gov.cshr.civilservant.dto.QuizSubmissionDto;
import uk.gov.cshr.civilservant.dto.SkillsReportsDto;
import uk.gov.cshr.civilservant.dto.SubmittedAnswerDto;
import uk.gov.cshr.civilservant.dto.factory.AnswerDtoFactory;
import uk.gov.cshr.civilservant.dto.factory.QuizDtoFactory;
import uk.gov.cshr.civilservant.dto.factory.QuizResultDtoFactory;
import uk.gov.cshr.civilservant.exception.ProfessionNotFoundException;
import uk.gov.cshr.civilservant.exception.QuizNotFoundException;
import uk.gov.cshr.civilservant.exception.QuizServiceException;
import uk.gov.cshr.civilservant.repository.OrganisationalUnitRepository;
import uk.gov.cshr.civilservant.repository.ProfessionRepository;
import uk.gov.cshr.civilservant.repository.QuizRepository;
import uk.gov.cshr.civilservant.repository.QuizResultRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;

@Service
@Transactional(
    propagation = Propagation.REQUIRES_NEW,
    rollbackFor = {QuizServiceException.class, QuizNotFoundException.class})
public class QuizService {

  private QuestionService questionService;
  private QuizRepository quizRepository;
  private ProfessionRepository professionRepository;
  private OrganisationalUnitRepository organisationalUnitRepository;
  private QuizResultRepository quizResultRepository;
  private QuizDtoFactory quizDtoFactory;
  private final AnswerDtoFactory answerDtoFactory;
  private QuizResultDtoFactory quizResultDtoFactory;

  @Autowired
  public QuizService(
      QuizRepository quizRepository,
      QuizDtoFactory quizDtoFactory,
      ProfessionRepository professionRepository,
      OrganisationalUnitRepository organisationalUnitRepository,
      QuestionService questionService,
      QuizResultRepository quizResultRepository,
      AnswerDtoFactory answerDtoFactory,
      QuizResultDtoFactory quizResultDtoFactory) {
    this.quizRepository = quizRepository;
    this.quizDtoFactory = quizDtoFactory;
    this.professionRepository = professionRepository;
    this.organisationalUnitRepository = organisationalUnitRepository;
    this.questionService = questionService;
    this.quizResultRepository = quizResultRepository;
    this.answerDtoFactory = answerDtoFactory;
    this.quizResultDtoFactory = quizResultDtoFactory;
  }

  public Optional<QuizDto> getQuizByProfessionIdAndOrganisationId(
      Long professionId, Long organisationId) {
    Optional<Quiz> quiz =
        quizRepository.findFirstByProfessionIdAndOrganisationIdAndStatusIsNot(
            professionId, organisationId, Status.INACTIVE);
    if (quiz.isPresent()) {
      final Set<Question> questionsToBeRemoved = collectDeletedQuestionsInAQuiz(quiz);
      quiz.get().getQuestions().removeAll(questionsToBeRemoved);
      return Optional.of(quizDtoFactory.create(quiz.get()));
    }
    return Optional.empty();
  }

  @Transactional
  public void delete(Long professionId, Long organisationId) {
    Optional<Quiz> optionalEntry =
        quizRepository.findFirstByProfessionIdAndOrganisationIdAndStatusIsNot(
            professionId, organisationId, Status.INACTIVE);
    if (!optionalEntry.isPresent()) {
      throw new EntityNotFoundException("No quiz found matching the id provided");
    } else {
      Quiz quizTobeDeleted = optionalEntry.get();
      quizTobeDeleted.setStatus(Status.INACTIVE);
      quizTobeDeleted.getQuestions().forEach(question -> question.setStatus(Status.INACTIVE));
      quizRepository.save(quizTobeDeleted);
    }
  }

  public QuizDto save(Quiz quiz) {
    return quizDtoFactory.create(quizRepository.save(quiz));
  }

  public QuizDto update(Quiz quiz, Long professionId, Long organisationId) {
    Optional<Quiz> quizToBeUpdated =
        quizRepository.findFirstByProfessionIdAndOrganisationIdAndStatusIsNot(
            professionId, organisationId, Status.INACTIVE);
    if (quizToBeUpdated.isPresent()) {
      Quiz quizEntry = quizToBeUpdated.get();
      quizEntry.setDescription(quiz.getDescription());
      quizEntry.setUpdatedOn(LocalDateTime.now());
      quizEntry = quizRepository.save(quizEntry);
      return quizDtoFactory.create(quizEntry);
    }
    return null;
  }

  public QuizDto create(Long professionId, Long organisationId)
      throws QuizServiceException, ProfessionNotFoundException {
    if (!organisationalUnitRepository.findById(organisationId).isPresent()) {
      throw new QuizServiceException("Invalid organisation supplied");
    }
    Optional<QuizDto> quizDto =
        getQuizByProfessionIdAndOrganisationId(professionId, organisationId);
    if (!quizDto.isPresent()) {
      Optional<Profession> profession = professionRepository.findById(professionId);
      if (!profession.isPresent()) {
        throw new ProfessionNotFoundException(
            String.format("Error creating quiz for non existent profession : %d", professionId));
      }
      return quizDtoFactory.create(
          quizRepository.save(
              Quiz.builder()
                  .status(Status.DRAFT)
                  .profession(profession.get())
                  .organisationId(organisationId)
                  .description("")
                  .createdOn(LocalDateTime.now())
                  .updatedOn(LocalDateTime.now())
                  .name("Quiz for " + profession.get().getName())
                  .numberOfQuestions(0)
                  .build()));
    }
    return quizDto.get();
  }

  public Optional<QuizDto> getQuizInfo(Long professionId, Long organisationId) {

    Optional<Quiz> quizRecord =
        quizRepository.findFirstByProfessionIdAndOrganisationIdAndStatusIs(
            professionId, organisationId, Status.PUBLISHED);
    if (quizRecord.isPresent()) {
      final Set<Question> questionsToBeRemoved = collectDeletedQuestionsInAQuiz(quizRecord);
      quizRecord.get().getQuestions().removeAll(questionsToBeRemoved);
      quizRecord.get().setNumberOfQuestions(quizRecord.get().getQuestions().size());
      return Optional.ofNullable(quizDtoFactory.mapSpecificFields(quizRecord.get()));
    }
    return Optional.empty();
  }

  public long publish(Quiz quiz) throws QuizServiceException {
    Optional<Quiz> quizToBePublished =
        quizRepository.findFirstByProfessionIdAndOrganisationIdAndStatusIsNot(
            quiz.getProfession().getId(), quiz.getOrganisationId(), Status.INACTIVE);
    if (quizToBePublished.isPresent()) {
      Set<Question> activeQuestions =
          quizToBePublished
              .get()
              .getQuestions()
              .stream()
              .filter(question -> !question.getStatus().equals(Status.INACTIVE))
              .collect(Collectors.toSet());
      if (activeQuestions.size() > 17) {
        quizToBePublished.get().setStatus(Status.PUBLISHED);
        quizToBePublished.get().setUpdatedOn(LocalDateTime.now());
        return save(quizToBePublished.get()).getId();
      } else {
        throw new QuizServiceException(
            "Quiz cannot be published. "
                + "A quiz must have 18 or more active questions before it can be published");
      }
    } else {
      throw new QuizServiceException("Quiz does not exist");
    }
  }

  public Optional<Long> submitAnswers(QuizSubmissionDto quizSubmissionDto)
      throws QuizServiceException {
    try {
      QuizResult result =
          QuizResult.builder()
              .quizId(quizSubmissionDto.getQuizId())
              .staffId(quizSubmissionDto.getStaffId())
              .professionId(quizSubmissionDto.getProfessionId())
              .organisationId(quizSubmissionDto.getOrganisationId())
              .quizName(quizSubmissionDto.getQuizName())
              .answers(new ArrayList<>())
              .type(quizSubmissionDto.getAnswers().length > 18 ? QuizType.LONG : QuizType.SHORT)
              .build();

      int correctCount = 0;

      for (SubmittedAnswerDto answersDto : quizSubmissionDto.getAnswers()) {
        correctCount = calculateResults(result, correctCount, answersDto);
      }

      result.setCorrectAnswers(correctCount);
      result.setNumberOfQuestions(quizSubmissionDto.getAnswers().length);
      result.setScore((correctCount * 100) / result.getNumberOfQuestions());
      result.setCompletedOn(LocalDateTime.now());
      return Optional.of(quizResultRepository.save(result).getId());
    } catch (Exception ex) {
      throw new QuizServiceException("Quiz submission failed " + ex.getMessage());
    }
  }

  @Transactional
  protected int calculateResults(
      QuizResult result, int correctCount, SubmittedAnswerDto answersDto) {
    Optional<Question> question =
        questionService.getByQuestionId((long) answersDto.getQuestionId());

    if (question.isPresent()) {
      SubmittedAnswer submittedAnswer = SubmittedAnswer.builder().question(question.get()).build();
      if (!answersDto.isSkipped()) {
        AnswerDto answerDto = answerDtoFactory.create(question.get().getAnswer());
        if (answerDto != null) {
          if (Arrays.deepEquals(answersDto.getSubmittedAnswers(), answerDto.getCorrectAnswers())) {
            submittedAnswer.setCorrect(true);
            submittedAnswer
                .getQuestion()
                .setCorrectCount(submittedAnswer.getQuestion().getCorrectCount() + 1);
            correctCount++;
          } else {
            submittedAnswer.setCorrect(false);
            submittedAnswer
                .getQuestion()
                .setIncorrectCount(submittedAnswer.getQuestion().getIncorrectCount() + 1);
          }
        }
        submittedAnswer.setSubmittedAnswers(answersDto.getSubmittedAnswers());
        submittedAnswer.setSkipped(false);
      } else {
        submittedAnswer.setSubmittedAnswers(new String[] {});
        submittedAnswer.setSkipped(true);
        submittedAnswer
            .getQuestion()
            .setSkippedCount(submittedAnswer.getQuestion().getSkippedCount() + 1);
      }
      question.get().setTimesAttempted(question.get().getTimesAttempted() + 1);
      submittedAnswer.setQuizResult(result);
      questionService.save(question.get());
      result.getAnswers().add(submittedAnswer);
    }
    return correctCount;
  }

  public QuizResultDto getQuizResult(Long quizResultId, String staffId)
      throws QuizServiceException {
    Optional<QuizResult> quizResultOptional =
        quizResultRepository.findQuizResultByIdAndStaffId(quizResultId, staffId);

    return quizResultOptional
        .map(quizResultDtoFactory::create)
        .orElseThrow(() -> new QuizServiceException("Failed to get results"));
  }

  public Optional<QuizHistoryDto> getQuizHistory(String staffId) {
    QuizHistoryDto history = QuizHistoryDto.builder().build();
    List<QuizResultDto> listOfQuizzesByUser = quizResultRepository.findQuizResultByStaffId(staffId);

    if (listOfQuizzesByUser != null && !listOfQuizzesByUser.isEmpty()) {
      history.setQuizResultDto(listOfQuizzesByUser);
    } else {
      return Optional.empty();
    }
    return Optional.of(history);
  }

  public Optional<List<QuizDataTableDto>> getAllResults() {
    List<QuizDataTableDto> quizDataTableDtoList = new ArrayList<>();
    List<QuizResultSummaryDto> quizResultSummaryDtos = quizResultRepository.findAllResults();

    populateDataTableDtos(quizDataTableDtoList, quizResultSummaryDtos);
    return Optional.of(quizDataTableDtoList);
  }

  public Optional<QuizDataTableDto> getAllResultsForProfessionInOrganisation(
      long professionId, long organisationId) {
    Optional<Profession> profession = professionRepository.findById(professionId);
    if (profession.isPresent()) {
      QuizResultSummaryDto quizResultDto =
          quizResultRepository.findByProfessionIdAndOrganisationId(professionId, organisationId);

      if (quizResultDto != null) {
        return Optional.ofNullable(populateDataTableDto(quizResultDto, profession.get().getName()));
      } else {
        return Optional.of(
            QuizDataTableDto.builder()
                .profession(profession.get().getName())
                .numberOfAttempts(0)
                .averageScore(0)
                .build());
      }
    }
    return Optional.empty();
  }

  public Optional<QuizDto> getQuiz(long professionId, long organisationId) {

    Optional<Quiz> quiz =
        quizRepository.findFirstByProfessionIdAndOrganisationIdAndStatusIsNot(
            professionId, organisationId, Status.INACTIVE);

    if (quiz.isPresent()) {
      final Set<Question> inactiveQuestions = collectDeletedQuestionsInAQuiz(quiz);
      quiz.get().getQuestions().removeAll(inactiveQuestions);
    }
    return quiz.map(quizDtoFactory::create);
  }

  public Optional<List<QuizDataTableDto>> getForAllProfessionsInTheOrganisation(
      long organisationId) {
    List<QuizDataTableDto> quizDataTableDtoList = new ArrayList<>();
    List<QuizResultSummaryDto> quizResultSummaryDtoList =
        quizResultRepository.findByOrOrganisationIdOrderByQuizNameAsc(organisationId);

    if (!quizResultSummaryDtoList.isEmpty()) {
      populateDataTableDtos(quizDataTableDtoList, quizResultSummaryDtoList);
    }
    return Optional.of(quizDataTableDtoList);
  }

  private Set<Question> collectDeletedQuestionsInAQuiz(Optional<Quiz> quiz) {
    return quiz.get()
        .getQuestions()
        .stream()
        .filter(question -> question.getStatus().equals(Status.INACTIVE))
        .collect(Collectors.toSet());
  }

  private void populateDataTableDtos(
      List<QuizDataTableDto> quizDataTableDtoList,
      List<QuizResultSummaryDto> quizResultSummaryDtos) {
    for (QuizResultSummaryDto quizResultSummaryDto : quizResultSummaryDtos) {

      final Optional<Profession> profession =
          professionRepository.findById(quizResultSummaryDto.getProfessionId());
      String professionName = "";
      if (profession.isPresent()) {
        professionName = profession.get().getName();
      }

      final QuizDataTableDto quizDataTableDto =
          populateDataTableDto(quizResultSummaryDto, professionName);

      quizDataTableDtoList.add(quizDataTableDto);
    }
  }

  private QuizDataTableDto populateDataTableDto(
      QuizResultSummaryDto quizResultSummaryDto, String professionName) {
    return QuizDataTableDto.builder()
        .profession(professionName)
        .averageScore(
            quizResultSummaryDto.getAverageScore() != null
                ? quizResultSummaryDto.getAverageScore().floatValue()
                : 0)
        .numberOfAttempts((int) quizResultSummaryDto.getNumberOfAttempts())
        .build();
  }

  public List<Long> deleteQuizResultsBetween(LocalDateTime from, LocalDateTime to) {
    List<QuizResult> quizResults = quizResultRepository.findAllByCompletedOnBetween(from, to);
    List<Long> resultsQualifiedForDeletion = quizResults.stream()
        .map(QuizResult :: getId).collect(Collectors.toList());
    quizResultRepository.deleteAll(quizResults);
    return resultsQualifiedForDeletion;
  }

  /** Methods for report extraction */
  public List<SkillsReportsDto> getReportForSuperAdmin(LocalDateTime from, LocalDateTime to) {
    List<QuizResult> quizResults = quizResultRepository.findAllByCompletedOnBetween(from, to);

    return extractReportFromResult(quizResults);
  }

  public List<SkillsReportsDto> getReportForOrganisationAdmin(
      long organisationId, LocalDateTime from, LocalDateTime to) {
    List<QuizResult> quizResults =
        quizResultRepository.findAllByOrganisationIdAndCompletedOnBetween(organisationId, from, to);

    return extractReportFromResult(quizResults);
  }

  public List<SkillsReportsDto> getReportForProfessionReporter(
      long organisationId, long professionId, LocalDateTime from, LocalDateTime to) {
    List<QuizResult> quizResults =
        quizResultRepository.findAllByOrganisationIdAndProfessionIdAndCompletedOnBetween(
            organisationId, professionId, from, to);

    return extractReportFromResult(quizResults);
  }

  public List<SkillsReportsDto> getReportForProfessionAdmin(
      long professionId, LocalDateTime from, LocalDateTime to) {
    List<QuizResult> quizResults =
        quizResultRepository.findAllByProfessionIdAndCompletedOnBetween(
            professionId, from, to);

    return extractReportFromResult(quizResults);
  }

  private List<SkillsReportsDto> extractReportFromResult(List<QuizResult> quizResults) {
    Set<Long> questionIds = new HashSet<>();

    quizResults.forEach(
        quizResult ->
            quizResult
                .getAnswers()
                .forEach(
                    submittedAnswer -> questionIds.add(submittedAnswer.getQuestion().getId())));

    List<Question> questionList = questionService.findAll(questionIds);

    return populateQuestionMetrics(questionList);
  }

  private List<SkillsReportsDto> populateQuestionMetrics(List<Question> questions) {
    List<SkillsReportsDto> skillsReportsDtoList = new ArrayList<>();

    questions.forEach(
        question -> {
          final SkillsReportsDto skillsReportsDto =
              SkillsReportsDto.builder()
                  .quizName(question.getQuiz().getName())
                  .status(question.getQuiz().getStatus())
                  .professionName(question.getQuiz().getProfession().getName())
                  .questionName(question.getValue())
                  .questionTheme(question.getTheme())
                  .questionId(question.getId())
                  .timesAttempted(question.getTimesAttempted())
                  .incorrectCount(question.getIncorrectCount())
                  .correctCount(question.getCorrectCount())
                  .skippedCount(question.getSkippedCount())
                  .build();
          skillsReportsDtoList.add(skillsReportsDto);
        });
    return skillsReportsDtoList;
  }
}
