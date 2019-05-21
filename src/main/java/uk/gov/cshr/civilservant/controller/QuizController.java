package uk.gov.cshr.civilservant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.cshr.civilservant.domain.Question;
import uk.gov.cshr.civilservant.domain.Quiz;
import uk.gov.cshr.civilservant.repository.QuizRepository;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/quizzes")
public class QuizController {

    @Autowired
    private QuizRepository quizRepository;

    @GetMapping
    public ResponseEntity<List<Question>> listQuestionsByProfession(@RequestParam Long professionId, @RequestParam(defaultValue = "10") Integer limit) {
        if (limit < 1 ) {
            return ResponseEntity.badRequest().build();
        }
        return quizRepository.findFirstByProfessionId(professionId)
                .map(quiz -> {
                    List<Question> questions = new ArrayList<>(quiz.getQuestions());
                    Collections.shuffle(questions);
                    if (questions.size() > limit) {
                        return ResponseEntity.ok(questions.stream().limit(limit).collect(Collectors.toList()));
                    }
                    return ResponseEntity.ok(questions);
                })
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity createQuiz(@Valid @RequestBody Quiz quiz) {
        quizRepository.deleteAllByProfessionId(quiz.getProfession().getId());
        quizRepository.save(quiz);
        return ResponseEntity.noContent().build();
    }
}
