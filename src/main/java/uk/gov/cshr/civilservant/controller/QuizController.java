package uk.gov.cshr.civilservant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cshr.civilservant.domain.Question;
import uk.gov.cshr.civilservant.domain.Quiz;
import uk.gov.cshr.civilservant.repository.QuizRepository;

import java.util.Set;

@RestController
@RequestMapping("/quizzes")
public class QuizController {

    @Autowired
    private QuizRepository quizRepository;

    @GetMapping
    public ResponseEntity<Set<Question>> listQuestionsByProfession(@RequestParam Long professionId, @RequestParam(defaultValue = "10") int limit) {
        Quiz professionQuiz = quizRepository.findFirstByProfessionId(professionId);
        return ResponseEntity.ok(professionQuiz.getQuestions());
    }
}
