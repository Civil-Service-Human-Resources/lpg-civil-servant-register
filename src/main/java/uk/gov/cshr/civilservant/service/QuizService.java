package uk.gov.cshr.civilservant.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.*;
import uk.gov.cshr.civilservant.dto.*;
import uk.gov.cshr.civilservant.dto.factory.QuestionDtoFactory;
import uk.gov.cshr.civilservant.dto.factory.QuizDtoFactory;
import uk.gov.cshr.civilservant.dto.factory.QuizResultDtoFactory;
import uk.gov.cshr.civilservant.exception.ProfessionNotFoundException;
import uk.gov.cshr.civilservant.exception.QuizNotFoundException;
import uk.gov.cshr.civilservant.exception.QuizServiceException;
import uk.gov.cshr.civilservant.repository.OrganisationalUnitRepository;
import uk.gov.cshr.civilservant.repository.ProfessionRepository;
import uk.gov.cshr.civilservant.repository.QuizRepository;
import uk.gov.cshr.civilservant.repository.QuizResultRepository;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {QuizServiceException.class, QuizNotFoundException.class})
public class QuizService {


  private QuestionService questionService;
  private QuizRepository quizRepository;
  private ProfessionRepository professionRepository;
  private OrganisationalUnitRepository organisationalUnitRepository;
  private QuizResultRepository quizResultRepository;
  private QuizDtoFactory quizDtoFactory;
  private QuestionDtoFactory questionDtoFactory;
  private QuizResultDtoFactory quizResultDtoFactory;

  @Autowired
  public QuizService(QuizRepository quizRepository,
                     QuizDtoFactory quizDtoFactory,
                     ProfessionRepository professionRepository,
                     OrganisationalUnitRepository organisationalUnitRepository,
                     QuestionService questionService,
                     QuizResultRepository quizResultRepository,
                     QuestionDtoFactory questionDtoFactory,
                     QuizResultDtoFactory quizResultDtoFactory) {
    this.quizRepository = quizRepository;
    this.quizDtoFactory = quizDtoFactory;
    this.professionRepository = professionRepository;
    this.organisationalUnitRepository = organisationalUnitRepository;
    this.questionService = questionService;
    this.quizResultRepository = quizResultRepository;
    this.questionDtoFactory = questionDtoFactory;
    this.quizResultDtoFactory = quizResultDtoFactory;
  }

  public Optional<QuizDto> getQuizByProfessionId(Long professionId) {
    Optional<Quiz> quiz = quizRepository
            .findFirstByProfessionIdAndStatusIsNot(professionId, Status.INACTIVE);
    if (quiz.isPresent()) {
      final Set<Question> questionsToBeRemoved = collectDeletedQuestionsInAQuiz(quiz);
      quiz.get().getQuestions().removeAll(questionsToBeRemoved);
      return Optional.of(quizDtoFactory.create(quiz.get()));
    }
    return Optional.empty();
  }

  @Transactional
  public void delete(Long professionId) {
    Optional<Quiz> optionalEntry = quizRepository.findFirstByProfessionIdAndStatusIsNot(professionId, Status.INACTIVE);
    if (!optionalEntry.isPresent()) {
      throw new EntityNotFoundException(
              "No quiz found matching the id provided");
    } else {
      Quiz quizTobeDeleted = optionalEntry.get();
      quizTobeDeleted.setStatus(Status.INACTIVE);
      quizTobeDeleted.getQuestions()
              .forEach(
                      question -> question.setStatus(Status.INACTIVE));
      quizRepository.save(quizTobeDeleted);
    }
  }

  public QuizDto save(Quiz quiz) {
    return quizDtoFactory.create(quizRepository.save(quiz));
  }

  public QuizDto update(Quiz quiz, Long professionId) {
    Optional<Quiz> quizToBeUpdated =
            quizRepository.findFirstByProfessionIdAndStatusIsNot(professionId, Status.INACTIVE);
    if (quizToBeUpdated.isPresent()) {
      Quiz quizEntry = quizToBeUpdated.get();
      quizEntry.setDescription(quiz.getDescription());
      quizEntry.setUpdatedOn(LocalDateTime.now());
      quizEntry = quizRepository.save(quizEntry);
      return quizDtoFactory.create(quizEntry);
    }
    return null;
  }

  public QuizDto create(Long professionId, Long organisationId) throws QuizServiceException, ProfessionNotFoundException {
    if (!organisationalUnitRepository.findById(organisationId).isPresent()) {
      throw new QuizServiceException("Invalid organisation supplied");
    }
    Optional<QuizDto> quizDto = getQuizByProfessionId(professionId);
    if (!quizDto.isPresent()) {
      Optional<Profession> profession = professionRepository.findById(professionId);
      if (!profession.isPresent()) {
        throw new ProfessionNotFoundException(
                String.format("Error creating quiz for non existent profession : %d"
                        ,professionId));
      }
      return quizDtoFactory.create(quizRepository.save(Quiz.builder()
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

  public Optional<QuizDto> getQuizInfo(Long professionId) {

    Optional<Quiz> quizRecord = quizRepository.findFirstByProfessionIdAndStatusIs(professionId, Status.PUBLISHED);
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
            quizRepository.findFirstByProfessionIdAndStatusIsNot(quiz.getProfession().getId(), Status.INACTIVE);
    if (quizToBePublished.isPresent()) {
      Set<Question> activeQuestions = quizToBePublished.get()
              .getQuestions()
              .stream()
              .filter(
                      question ->
                              !question.getStatus().equals(Status.INACTIVE))
              .collect(Collectors.toSet());
      if (activeQuestions.size() > 17) {
        quizToBePublished.get().setStatus(Status.PUBLISHED);
        quizToBePublished.get().setUpdatedOn(LocalDateTime.now());
        return save(quizToBePublished.get()).getId();
      } else {
        throw new QuizServiceException("Quiz cannot be published. " +
                "A quiz must have 18 or more active questions before it can be published");
      }
    } else {
      throw new QuizServiceException("Quiz does not exist");
    }
  }

  public Optional<Long> submitAnswers(QuizSubmissionDto quizSubmissionDto) throws QuizServiceException {
    try {
      QuizResult result = QuizResult.builder()
              .quizId(quizSubmissionDto.getQuizId())
              .staffId(quizSubmissionDto.getStaffId())
              .professionId(quizSubmissionDto.getProfessionId())
              .organisationId(quizSubmissionDto.getOrganisationId())
              .quizName(quizSubmissionDto.getQuizName())
              .answers(new ArrayList<>())
              .type(quizSubmissionDto.getAnswers().length > 18
                      ? QuizType.LONG : QuizType.SHORT)
              .build();

      int correctCount = 0;

      for (SubmittedAnswerDto answersDto: quizSubmissionDto.getAnswers()) {
        correctCount = calculateResults(result, correctCount, answersDto);
      }

      result.setCorrectAnswers(correctCount);
      result.setNumberOfQuestions(quizSubmissionDto.getAnswers().length);
      result.setScore((correctCount*100)/result.getNumberOfQuestions());
      result.setCompletedOn(LocalDateTime.now());
      return Optional.of(quizResultRepository.save(result).getId());
    } catch (Exception ex) {
      throw new QuizServiceException("Quiz submission failed " + ex.getMessage());
    }
  }

  private int calculateResults(QuizResult result, int correctCount, SubmittedAnswerDto answersDto) {
    Optional<QuestionDto> question = questionService.getById((long) answersDto.getQuestionId());
    if (question.isPresent()) {
      SubmittedAnswer submittedAnswer = SubmittedAnswer.builder()
              .question(questionDtoFactory.createEntity(question.get()))
              .build();
      if (!answersDto.isSkipped()) {
        AnswerDto answerDto = question.get().getAnswer();
        if (answerDto != null) {
          if (Arrays.deepEquals(answersDto.getSubmittedAnswers(), answerDto.getCorrectAnswers())) {
            correctCount++;
          }
        }
        submittedAnswer.setSubmittedAnswers(answersDto.getSubmittedAnswers());
        submittedAnswer.setSkipped(false);
      }else {
        submittedAnswer.setSubmittedAnswers(new String[]{});
        submittedAnswer.setSkipped(true);
      }
      submittedAnswer.setQuizResult(result);
      result.getAnswers().add(submittedAnswer);
    }
    return correctCount;
  }

  public QuizResultDto getQuizResult(Long quizResultId, String staffId) throws QuizServiceException {
    Optional<QuizResult> quizResultOptional = quizResultRepository.findQuizResultByIdAndStaffId(quizResultId, staffId);

    return quizResultOptional
            .map(quizResultDtoFactory:: create)
            .orElseThrow(() -> new QuizServiceException("Failed to get results"));
  }

  public Optional<QuizHistoryDto> getQuizHistory(String staffId) {
    QuizHistoryDto history = QuizHistoryDto.builder().build();
    List<QuizResultDto> listOfQuizzesByUser =
            quizResultRepository.findQuizResultByStaffId(staffId);

    if (listOfQuizzesByUser!=null &&
    !listOfQuizzesByUser.isEmpty()) {
      history.setQuizResultDto(listOfQuizzesByUser);
    } else {
      return Optional.empty();
    }
    return Optional.of(history);
  }

  public Optional<List<QuizDataTableDto>> getAllResults () {
    List<QuizDataTableDto>  quizDataTableDtoList = new ArrayList<>();
    List<QuizResultSummaryDto> quizResultSummaryDtos = quizResultRepository.findAllResults();

    populateDataTableDtos(quizDataTableDtoList, quizResultSummaryDtos);
    return Optional.of(quizDataTableDtoList);
  }

  public Optional<QuizDataTableDto> getAllResultsForProfession (long professionId) {
    Optional<Profession> profession = professionRepository.findById(professionId);
    if (profession.isPresent()) {
      QuizResultSummaryDto quizResultDto = quizResultRepository.findByProfessionId(professionId);

      if (quizResultDto != null) {
        return Optional.ofNullable(populateDataTableDto(quizResultDto, profession.get().getName()));
      } else {
        return Optional.of(QuizDataTableDto.builder()
                .profession(profession.get().getName())
                .numberOfAttempts(0).averageScore(0).build());
      }
    }
    return Optional.empty();
  }

  public Optional<QuizDto> getQuiz(long quizId) {

    Optional<Quiz> quiz = quizRepository
            .findFirstByProfessionIdAndStatusIsNot(
                    quizId,
                    Status.INACTIVE);

    if (quiz.isPresent()) {
      final Set<Question> inactiveQuestions = collectDeletedQuestionsInAQuiz(quiz);
      quiz.get().getQuestions().removeAll(inactiveQuestions);
    }
    return quiz.map(quizDtoFactory::create);
  }

  public Optional<List<QuizDataTableDto>> getForAllProfessionsInTheOrganisation(long organisationId)
  {
    List<QuizDataTableDto>  quizDataTableDtoList = new ArrayList<>();
    List<QuizResultSummaryDto> quizResultSummaryDtoList = quizResultRepository.findByOrOrganisationIdOrderByQuizNameAsc(organisationId);

    if (!quizResultSummaryDtoList.isEmpty()) {
      populateDataTableDtos(quizDataTableDtoList, quizResultSummaryDtoList);
    }
    return Optional.of(quizDataTableDtoList);
  }

  private Set<Question> collectDeletedQuestionsInAQuiz(Optional<Quiz> quiz) {
    return quiz.get()
            .getQuestions()
            .stream()
            .filter(
                    question ->
                            question.getStatus().equals(Status.INACTIVE))
            .collect(Collectors.toSet());
  }

  private void populateDataTableDtos(List<QuizDataTableDto> quizDataTableDtoList,
                                     List<QuizResultSummaryDto> quizResultSummaryDtos) {
    for (QuizResultSummaryDto quizResultSummaryDto: quizResultSummaryDtos){

      final Optional<Profession> profession = professionRepository.findById(quizResultSummaryDto.getProfessionId());
      String professionName = "";
      if (profession.isPresent()) {
        professionName = profession.get().getName();
      }

      final QuizDataTableDto quizDataTableDto = populateDataTableDto(quizResultSummaryDto, professionName);

      quizDataTableDtoList.add(quizDataTableDto);
    }
  }

  private QuizDataTableDto populateDataTableDto(QuizResultSummaryDto quizResultSummaryDto,
                                                String professionName) {
    return QuizDataTableDto.builder()
            .profession(professionName)
            .averageScore(quizResultSummaryDto.getAverageScore()!= null ?
                    quizResultSummaryDto.getAverageScore().floatValue() : 0)
            .numberOfAttempts((int) quizResultSummaryDto.getNumberOfAttempts())
            .build();
  }
}
