package uk.gov.cshr.civilservant.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cshr.civilservant.domain.Profession;
import uk.gov.cshr.civilservant.domain.Quiz;
import uk.gov.cshr.civilservant.domain.QuizResult;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class QuizResultRepositoryTest {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private ProfessionRepository professionRepository;

    @Autowired
    private QuizResultRepository quizResultRepository;

    private Quiz testQuiz;

    @Before
    public void setupQuizData() {
        Profession testProffesion = new Profession();
        testProffesion.setName("Test Profession - Quiz Result Test");
        testProffesion = professionRepository.save(testProffesion);

        Quiz testQuiz = Quiz.builder().name("Test Quiz - Quiz Result Test").profession(testProffesion).build();
        this.testQuiz = quizRepository.save(testQuiz);
    }

    @After
    public void clearQuizData() {
        quizRepository.deleteAll();
    }

    @Test
    public void deleteQuizResultsByCompletedOnIsLessThanEqual_resultRecordCompletedBeforeSuppliedTimeShouldBeDeleted() {
        quizResultRepository.save(QuizResult.builder().quizId(this.testQuiz.getId()).correctAnswers(1).build());
        List<QuizResult> quizResult = quizResultRepository.findAll();
        assertEquals(1, quizResult.size());

        long deleteCount = quizResultRepository.deleteQuizResultsByCompletedOnIsLessThanEqual(LocalDateTime.now().plusHours(1));
        assertEquals(1, deleteCount);

        quizResult = quizResultRepository.findAll();
        assertEquals(0, quizResult.size());
    }

    @Test
    public void deleteQuizResultsByCompletedOnIsLessThanEqual_resultRecordCompletedAfterSuppliedTimeShouldNotBeDeleted() {
        quizResultRepository.save(QuizResult.builder().quizId(this.testQuiz.getId()).correctAnswers(1).build());
        List<QuizResult> quizResult = quizResultRepository.findAll();
        assertEquals(1, quizResult.size());

        long deleteCount = quizResultRepository.deleteQuizResultsByCompletedOnIsLessThanEqual(LocalDateTime.now().minusHours(1));
        assertEquals(0, deleteCount);

        quizResult = quizResultRepository.findAll();
        assertEquals(1, quizResult.size());
    }

    @Test
    public void deleteQuizResultsByCompletedOnIsLessThanEqual_mix() throws InterruptedException {
        quizResultRepository.save(QuizResult.builder().quizId(this.testQuiz.getId()).correctAnswers(1).build());
        // Default MySQL TIMESTAMP resolution is seconds, ensure at least one second has past to have different timestamps
        Thread.sleep(1001);
        LocalDateTime postWaitTime = LocalDateTime.now();
        Thread.sleep(1001);
        quizResultRepository.save(QuizResult.builder().quizId(this.testQuiz.getId()).correctAnswers(2).build());

        List<QuizResult> quizResult = quizResultRepository.findAll();
        assertEquals(2, quizResult.size());

        long deleteCount = quizResultRepository.deleteQuizResultsByCompletedOnIsLessThanEqual(postWaitTime);
        assertEquals(1, deleteCount);

        quizResult = quizResultRepository.findAll();
        assertEquals(1, quizResult.size());
    }

    @Test
    public void deleteQuizResultsByCompletedOnIsLessThanEqual_resultRecordCompletedAtSuppliedTimeShouldBeDeleted() {
        QuizResult quizResult = quizResultRepository.save(QuizResult.builder().quizId(this.testQuiz.getId()).correctAnswers(1).build());
        List<QuizResult> allQuizResults = quizResultRepository.findAll();
        assertEquals(1, allQuizResults.size());

        long deleteCount = quizResultRepository.deleteQuizResultsByCompletedOnIsLessThanEqual(quizResult.getCompletedOn());
        assertEquals(1, deleteCount);

        allQuizResults = quizResultRepository.findAll();
        assertEquals(0, allQuizResults.size());
    }
}