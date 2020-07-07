package uk.gov.cshr.civilservant.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.domain.Profession;
import uk.gov.cshr.civilservant.domain.Question;
import uk.gov.cshr.civilservant.domain.Quiz;
import uk.gov.cshr.civilservant.domain.QuizResult;
import uk.gov.cshr.civilservant.domain.Status;
import uk.gov.cshr.civilservant.domain.SubmittedAnswer;
import uk.gov.cshr.civilservant.dto.QuizDataTableDto;
import uk.gov.cshr.civilservant.dto.QuizDto;
import uk.gov.cshr.civilservant.dto.QuizResultSummaryDto;
import uk.gov.cshr.civilservant.dto.SkillsReportsDto;
import uk.gov.cshr.civilservant.dto.factory.QuizDtoFactory;
import uk.gov.cshr.civilservant.exception.ProfessionNotFoundException;
import uk.gov.cshr.civilservant.exception.QuizServiceException;
import uk.gov.cshr.civilservant.repository.*;

@RunWith(MockitoJUnitRunner.class)
public class QuizServiceTest {

    @Mock
    QuizRepository quizRepository;

    @Mock
    QuestionService questionService;

    @Mock
    QuizResultRepository quizResultRepository;

    @Mock
    OrganisationalUnitRepository organisationalUnitRepository;

    @Mock
    QuizDtoFactory quizDtoFactory;

    @Mock
    ProfessionRepository professionRepository;

    @InjectMocks
    QuizService quizService;

    @Test
    public void shouldGetProfessionById() {
        //Given
        Long professionId = 1L;
        Long organisationId = 1L;
        Quiz quiz = QuizBuilder.buildEntity();
        QuizDto quizDTO = QuizBuilder.buildQuizDTO().get();
        //When
        when(quizRepository.findFirstByProfessionIdAndOrganisationIdAndStatusIsNot(professionId, organisationId, Status.INACTIVE)).thenReturn(Optional.of(quiz));
        when(quizDtoFactory.create(quiz)).thenReturn(quizDTO);

        //then

        QuizDto expected = quizService
                .getQuizByProfessionIdAndOrganisationId(professionId, organisationId)
                .get();
        assertTrue(expected.equals(quizDTO));

    }

    @Test
    public void shouldGetAllQuizzesInTheSystem() {
        //Given
        QuizResultSummaryDto quizResultSummaryDto = QuizResultSummaryDto.builder()
                .averageScore(23.0)
                .numberOfAttempts(2)
                .professionId(1).build();
        QuizDataTableDto quizDataTableDto = QuizDataTableDto.builder()
                .profession("")
                .averageScore(23)
                .numberOfAttempts(2).build();
        //When
        when(quizResultRepository.findAllResults()).thenReturn(Arrays.asList(quizResultSummaryDto));

        //then
        Optional<List<QuizDataTableDto>> expected = quizService.getAllResults();
        assertTrue(expected.get().get(0).getProfession().equals(quizDataTableDto.getProfession()));
        assertTrue(expected.get().get(0).getNumberOfAttempts() == quizDataTableDto.getNumberOfAttempts());
        assertTrue(expected.get().get(0).getAverageScore() == quizDataTableDto.getAverageScore());
    }

    @Test
    public void shouldReturnNothingWhenNothingFoundForGetProfessionById() {
        //Given
        Long professionId = 1L;
        Long organisationId = 1L;

        //When
        when(quizRepository.findFirstByProfessionIdAndOrganisationIdAndStatusIsNot(professionId, organisationId, Status.INACTIVE)).thenReturn(Optional.empty());

        //then

        assertTrue(quizService.getQuizByProfessionIdAndOrganisationId(professionId, organisationId).equals(Optional.empty()));
    }

    @Test
    public void shouldPersistQuizzesInTheSystem() {
        //Given
        Quiz quiz = QuizBuilder.buildEntity();
        QuizDto quizDTO = QuizBuilder.buildQuizDTO().get();

        //When
        when(quizRepository.save(quiz)).thenReturn(quiz);
        when(quizDtoFactory.create(quiz)).thenReturn(quizDTO);

        //then
        QuizDto expected = quizService.save(quiz);
        assertTrue(expected.equals(quizDTO));
    }

    @Test
    public void shouldDeleteQuizzesInTheSystem() {
        //Given
        Long professionId = 1L;
        Long organisationId = 1L;
        Quiz quiz = QuizBuilder.buildEntity();

        //when
        when(quizRepository.findFirstByProfessionIdAndOrganisationIdAndStatusIsNot(professionId, organisationId, Status.INACTIVE)).thenReturn(Optional.of(quiz));

        //then
        quizService.delete(professionId, organisationId);
        verify(quizRepository, times(1)).save(quiz);
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowExceptionIfActiveQuizNotFound() {
        //Given
        Long professionId = 1L;
        Long organisationId = 1L;

        //when
        when(quizRepository.findFirstByProfessionIdAndOrganisationIdAndStatusIsNot(professionId, organisationId, Status.INACTIVE)).thenReturn(Optional.empty());

        //then
        quizService.delete(professionId, organisationId);
    }

    @Test
    public void shouldUpdateQuizDescription() {
        //Given
        Long professionId = 1L;
        Long organisationId = 1L;
        Quiz quiz = QuizBuilder.buildEntity();
        QuizDto quizDto = QuizBuilder.buildQuizDTO().get();
        quizDto.setDescription("Test update");
        //when
        when(quizRepository.findFirstByProfessionIdAndOrganisationIdAndStatusIsNot(professionId, organisationId, Status.INACTIVE)).thenReturn(Optional.of(quiz));
        when(quizRepository.save(quiz)).thenReturn(quiz);
        when(quizDtoFactory.create(quiz)).thenReturn(quizDto);

        quiz.setDescription("Test update");
        //then
        QuizDto updatedQuiz = quizService.update(quiz, professionId, organisationId);
        verify(quizRepository, times(1)).save(quiz);
        assertEquals("Test update", updatedQuiz.getDescription());
    }

    @Test
    public void shouldReturnExistingQuizFromCreateQuiz() throws QuizServiceException, ProfessionNotFoundException {
        //Given
        Quiz quiz = QuizBuilder.buildEntity();
        QuizDto quizDTO = QuizBuilder.buildQuizDTO().get();

        //When
        when(quizRepository
                .findFirstByProfessionIdAndOrganisationIdAndStatusIsNot(anyLong(), anyLong(), any()))
                .thenReturn(Optional.of(quiz));
        when(quizDtoFactory.create(quiz)).thenReturn(quizDTO);
        when(organisationalUnitRepository.findById(anyLong()))
                .thenReturn(Optional.of(new OrganisationalUnit()));

        //then
        QuizDto expected = quizService.create(1L, 1L);
        assertTrue(expected.equals(quizDTO));
    }

    @Test
    public void shouldCreateNewQuizIfNoneFoundForProfession() throws QuizServiceException, ProfessionNotFoundException {
        //Given
        Quiz quiz = QuizBuilder.buildEntity();
        QuizDto quizDTO = QuizBuilder.buildQuizDTO().get();

        //When
        when(organisationalUnitRepository.findById(anyLong()))
                .thenReturn(Optional.of(new OrganisationalUnit()));
        when(professionRepository.findById(anyLong()))
                .thenReturn(Optional.of(Profession.builder().build()));
        when(quizRepository
                .findFirstByProfessionIdAndOrganisationIdAndStatusIsNot(anyLong(), anyLong(), any()))
                .thenReturn(Optional.empty());
        when(quizRepository.save(any())).thenReturn(quiz);
        when(quizDtoFactory.create(any())).thenReturn(quizDTO);

        //then
        QuizDto expected = quizService.create(1L, 1L);
        assertTrue(expected.equals(quizDTO));
    }

  @Test
  public void shouldReturnReportsForSuperAdmin() {
    //Given

    //when
    when(quizResultRepository.findAllByCompletedOnBetween(any(), any())).thenReturn(buildSomeResults());
    when(questionService.findAll(anySet())).thenReturn(buildSomeQuestions());
    //then
    List<SkillsReportsDto> expectedList = quizService.getReportForSuperAdmin(any(), any());

    assertTrue(expectedList.size() > 0);
    assertEquals(1, expectedList.get(1).getQuestionId());
  }

  @Test
  public void shouldReturnReportsForOrgAdmin() {
    //Given

    //when
    when(quizResultRepository.findAllByOrganisationIdAndCompletedOnBetween(anyLong(), any(), any())).thenReturn(buildSomeResults());
    when(questionService.findAll(anySet())).thenReturn(buildSomeQuestions());
    //then
    List<SkillsReportsDto> expectedList =
        quizService.getReportForOrganisationAdmin(anyLong(),any(), any());

    assertTrue(expectedList.size() > 0);
    assertEquals(1, expectedList.get(1).getQuestionId());
  }

  @Test
  public void shouldReturnReportsForProfReporter() {
    //Given

    //when
    when(quizResultRepository
        .findAllByOrganisationIdAndProfessionIdAndCompletedOnBetween(
            anyLong(), anyLong(), any(), any()))
        .thenReturn(buildSomeResults());
    when(questionService.findAll(anySet())).thenReturn(buildSomeQuestions());
    //then
    List<SkillsReportsDto> expectedList =
        quizService.getReportForProfessionReporter(anyLong(), anyLong(), any(), any());

    assertTrue(expectedList.size() > 0);
    assertEquals(1, expectedList.get(1).getQuestionId());
  }

  @Test
  public void shouldReturnReportsForProfAdmin() {
    //Given

    //when
    when(quizResultRepository
        .findAllByProfessionIdAndCompletedOnBetween(
            anyLong(), any(), any()))
        .thenReturn(buildSomeResults());
    when(questionService.findAll(anySet())).thenReturn(buildSomeQuestions());
    //then
    List<SkillsReportsDto> expectedList =
        quizService.getReportForProfessionAdmin(anyLong(), any(), any());

    assertTrue(expectedList.size() > 0);
    assertEquals(1, expectedList.get(1).getQuestionId());
  }

  private List<Question> buildSomeQuestions() {
    List<Question> questionList = new ArrayList<>();
    for (int i=0; i < 5; i++) {
      Question question = Question.builder()
          .id((long) i)
          .quiz(Quiz.builder()
              .name("Some name")
              .profession(Profession.builder().build())
              .build())
          .status(Status.ACTIVE)
          .alternativeText("")
          .correctCount(i)
          .incorrectCount(i)
          .skippedCount(i)
          .theme("Some theme")
          .value("Some text")
          .build();
      questionList.add(question);
    }
    return questionList;
  }

  private List<QuizResult> buildSomeResults() {
    List<QuizResult> results = new ArrayList<>();
      for (int i=0; i < 5; i++) {
        QuizResult result = QuizResult.builder()
            .organisationId(1)
            .professionId(1)
            .quizName("Some name")
            .answers(buildSomeSubmittedAnswers()).build();
        results.add(result);
      }
      return results;
  }

  private List<SubmittedAnswer> buildSomeSubmittedAnswers() {
    List<SubmittedAnswer> submittedAnswers = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        SubmittedAnswer submittedAnswer = SubmittedAnswer.builder()
            .question(
                Question.builder().id((long)i).build()
            ).build();
        submittedAnswers.add(submittedAnswer);
      }
      return submittedAnswers;
  }
}
