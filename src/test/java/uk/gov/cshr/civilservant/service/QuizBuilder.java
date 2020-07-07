package uk.gov.cshr.civilservant.service;

import java.util.*;

import uk.gov.cshr.civilservant.domain.*;
import uk.gov.cshr.civilservant.dto.AnswerDto;
import uk.gov.cshr.civilservant.dto.QuestionDto;
import uk.gov.cshr.civilservant.dto.QuizDto;

public class QuizBuilder {

    public static Optional<QuizDto> buildQuizDTO() {
        Map<String,String> answerMap = new LinkedHashMap<>();
        answerMap.put("A", "Answer 1");
        answerMap.put("B", "Answer 2");
        AnswerDto answer = AnswerDto.builder()
                .answers(answerMap)
                .correctAnswers(new String[]{"B"})
                .build();
        QuestionDto question1 = QuestionDto.builder()
                .answer(answer)
                .suggestions("Some suggestion")
                .value("Question for quiz")
                .type(QuestionType.MULTIPLE)
                .status(Status.DRAFT)
                .id(1L)
                .build();
        Set<QuestionDto> questions = new HashSet<>();
        questions.add(question1);
        Profession profession = new Profession();
        profession.setId(1L);
        return Optional.of(QuizDto.builder()
                .profession(profession)
                .questions(questions)
                .build());

    }

    public static Quiz buildEntity() {
        Question question1 = buildAQuestionEntity();
        Set<Question> questions = new HashSet<>();
        questions.add(question1);
        Profession profession = new Profession();
        profession.setId(1L);
        return Quiz.builder()
                .id(1L)
                .profession(profession)
                .questions(questions)
                .status(Status.DRAFT)
                .build();
    }

    public static QuestionDto buildAQuestion(long quizId) {
        QuizDto quiz = new QuizDto();
        quiz.setId(quizId);
        Map<String,String> answerMap = new LinkedHashMap<>();
        answerMap.put("A", "Answer 1");
        answerMap.put("B", "Answer 2");
        AnswerDto answer = AnswerDto.builder()
                .id(1L)
                .answers(answerMap)
                .correctAnswers(new String[]{"B"})
                .build();
        QuestionDto question = QuestionDto.builder()
                .answer(answer)
                .suggestions("Some suggestion")
                .value("Question for quiz")
                .type(QuestionType.MULTIPLE)
                .status(Status.DRAFT)
                .id(1L)
                .build();
        return question;
    }

    public static Question buildAQuestionEntity() {
        return Question.builder()
                .quiz(Quiz.builder().status(Status.DRAFT).build())
                .status(Status.ACTIVE)
                .answer(buildAnAnswer())
                .suggestions("Some suggestion")
                .value("Question for quiz")
                .type(QuestionType.MULTIPLE)
                .id(1L)
                .build();
    }

    public static Answer buildAnAnswer() {
        Map<String,Object> answerMap = new LinkedHashMap<>();
        answerMap.put("A", "Answer 1");
        answerMap.put("B", "Answer 2");
        return Answer.builder()
                .answers(answerMap)
                .correctAnswer("B")
                .build();
    }
}
