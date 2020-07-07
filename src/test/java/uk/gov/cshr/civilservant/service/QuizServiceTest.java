package uk.gov.cshr.civilservant.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

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
import uk.gov.cshr.civilservant.domain.Quiz;
import uk.gov.cshr.civilservant.domain.Status;
import uk.gov.cshr.civilservant.dto.QuizDataTableDto;
import uk.gov.cshr.civilservant.dto.QuizDto;
import uk.gov.cshr.civilservant.dto.QuizResultSummaryDto;
import uk.gov.cshr.civilservant.dto.factory.QuizDtoFactory;
import uk.gov.cshr.civilservant.repository.*;

@RunWith(MockitoJUnitRunner.class)
public class QuizServiceTest {

    @Mock
    QuizRepository quizRepository;

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
        Quiz quiz = QuizBuilder.buildEntity();
        QuizDto quizDTO = QuizBuilder.buildQuizDTO().get();
        //When
        when(quizRepository.findFirstByProfessionIdAndStatusIsNot(professionId, Status.INACTIVE)).thenReturn(Optional.of(quiz));
        when(quizDtoFactory.create(quiz)).thenReturn(quizDTO);

        //then

        QuizDto expected = quizService.getQuizByProfessionId(professionId).get();
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

        //When
        when(quizRepository.findFirstByProfessionIdAndStatusIsNot(professionId, Status.INACTIVE)).thenReturn(Optional.empty());

        //then

        assertTrue(quizService.getQuizByProfessionId(professionId).equals(Optional.empty()));
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
        Quiz quiz = QuizBuilder.buildEntity();

        //when
        when(quizRepository.findFirstByProfessionIdAndStatusIsNot(professionId, Status.INACTIVE)).thenReturn(Optional.of(quiz));

        //then
        quizService.delete(professionId);
        verify(quizRepository, times(1)).save(quiz);
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowExceptionIfActiveQuizNotFound() {
        //Given
        Long professionId = 1L;

        //when
        when(quizRepository.findFirstByProfessionIdAndStatusIsNot(professionId, Status.INACTIVE)).thenReturn(Optional.empty());

        //then
        quizService.delete(professionId);
    }

    @Test
    public void shouldUpdateQuizDescription() {
        //Given
        Long professionId = 1L;
        Quiz quiz = QuizBuilder.buildEntity();
        QuizDto quizDto = QuizBuilder.buildQuizDTO().get();
        quizDto.setDescription("Test update");
        //when
        when(quizRepository.findFirstByProfessionIdAndStatusIsNot(professionId, Status.INACTIVE)).thenReturn(Optional.of(quiz));
        when(quizRepository.save(quiz)).thenReturn(quiz);
        when(quizDtoFactory.create(quiz)).thenReturn(quizDto);

        quiz.setDescription("Test update");
        //then
        QuizDto updatedQuiz = quizService.update(quiz, professionId);
        verify(quizRepository, times(1)).save(quiz);
        assertEquals("Test update", updatedQuiz.getDescription());
    }

    @Test
    public void shouldReturnExistingQuizFromCreateQuiz() {
        //Given
        Quiz quiz = QuizBuilder.buildEntity();
        QuizDto quizDTO = QuizBuilder.buildQuizDTO().get();

        //When
        when(quizRepository.findFirstByProfessionIdAndStatusIsNot(anyLong(), any())).thenReturn(Optional.of(quiz));
        when(quizDtoFactory.create(quiz)).thenReturn(quizDTO);
        when(organisationalUnitRepository.findById(anyLong())).thenReturn(Optional.of(new OrganisationalUnit()));

        //then
        QuizDto expected = quizService.create(1L, 1L);
        assertTrue(expected.equals(quizDTO));
    }

    @Test
    public void shouldCreateNewQuizIfNoneFoundForProfession() {
        //Given
        Quiz quiz = QuizBuilder.buildEntity();
        QuizDto quizDTO = QuizBuilder.buildQuizDTO().get();

        //When
        when(organisationalUnitRepository.findById(anyLong())).thenReturn(Optional.of(new OrganisationalUnit()));
        when(professionRepository.findById(anyLong())).thenReturn(Optional.of(Profession.builder().build()));
        when(quizRepository.findFirstByProfessionIdAndStatusIsNot(anyLong(), any())).thenReturn(Optional.empty());
        when(quizRepository.save(any())).thenReturn(quiz);
        when(quizDtoFactory.create(any())).thenReturn(quizDTO);

        //then
        QuizDto expected = quizService.create(1L, 1L);
        assertTrue(expected.equals(quizDTO));
    }

}
