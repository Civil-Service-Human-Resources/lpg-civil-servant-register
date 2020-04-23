package uk.gov.cshr.civilservant.service;

import java.util.Optional;
import javax.persistence.EntityNotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.*;
import uk.gov.cshr.civilservant.dto.AnswerDto;
import uk.gov.cshr.civilservant.dto.QuestionDto;
import uk.gov.cshr.civilservant.dto.factory.AnswerDtoFactory;
import uk.gov.cshr.civilservant.dto.factory.QuestionDtoFactory;
import uk.gov.cshr.civilservant.exception.QuizNotFoundException;
import uk.gov.cshr.civilservant.exception.QuizServiceException;
import uk.gov.cshr.civilservant.repository.AnswerRepository;
import uk.gov.cshr.civilservant.repository.QuestionRepository;
import uk.gov.cshr.civilservant.repository.QuizRepository;

@Slf4j
@Service
@Transactional
public class QuestionService {
    private QuizRepository quizRepository;
    private QuestionRepository questionRepository;
    private AnswerRepository answerRepository;
    private QuestionDtoFactory questionDTOFactory;
    private AnswerDtoFactory answerDtoFactory;

    @Autowired
    public QuestionService (QuestionRepository questionRepository,
                            AnswerRepository answerRepository,
                            QuestionDtoFactory questionDTOFactory,
                            AnswerDtoFactory answerDtoFactory,
                            QuizRepository quizRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.questionDTOFactory = questionDTOFactory;
        this.answerDtoFactory = answerDtoFactory;
        this.quizRepository = quizRepository;
    }

    /**
     *
     * @param questionDTO
     * @return The updated quiz identifier.
     */
    public Long updateQuizQuestion(QuestionDto questionDTO) {
        Optional<Question> questionToBeUpdated = questionRepository.findById(questionDTO.getId());
        if (questionToBeUpdated.isPresent()) {
            Quiz quizRecord = questionToBeUpdated.get().getQuiz();
            if (quizRecord.getStatus().equals(Status.INACTIVE)) {
                throw new QuizNotFoundException("Cannot update question. Reason: Archived Quiz");
            }

            if (questionToBeUpdated.get().getStatus().equals(Status.INACTIVE)) {
                throw new QuizServiceException("Cannot update a deleted question");
            }
            Question question = questionDTOFactory.createEntity(questionDTO);
            populateQuestion(questionDTO, questionToBeUpdated, question);
            return questionRepository.save(questionToBeUpdated.get()).getId();
        }
        throw new QuizServiceException("Question not found.");
    }

    private void populateQuestion(QuestionDto questionDTO, Optional<Question> questionToBeUpdated, Question question) {
        questionToBeUpdated.get().setValue(question.getValue());
        questionToBeUpdated.get().setImgUrl(question.getImgUrl());
        questionToBeUpdated.get().setSuggestions(question.getSuggestions());
        questionToBeUpdated.get().setTheme(question.getTheme());
        questionToBeUpdated.get().setWhy(question.getWhy());
        AnswerDto answerDto = questionDTO.getAnswer();
        if (answerDto.getCorrectAnswers().length > 1) {
            questionToBeUpdated.get().setType(QuestionType.MULTIPLE);
        } else {
            questionToBeUpdated.get().setType(QuestionType.SINGLE);
        }
        if (answerDto.getId()!= null) {
            Optional<Answer> answer = answerRepository.findById(answerDto.getId());
            if (answer.isPresent()) {
                answer = Optional.of(answerDtoFactory.createEntity(answerDto));
                answer.get().setQuestion(questionToBeUpdated.get());
            }
        } else {
            Answer newAnswer = answerDtoFactory.createEntity(answerDto);
            newAnswer.setQuestion(questionToBeUpdated.get());
            questionToBeUpdated.get().setAnswer(newAnswer);
        }
    }

    public Long addQuizQuestion(long professionId, QuestionDto questionDTO) {
        if (!StringUtils.isEmpty(questionDTO.getImgUrl())
                && StringUtils.isEmpty(questionDTO.getAlternativeText())) {
            throw new QuizServiceException("Alternative text value required for image added.");
        }

        Optional<Quiz> quiz = quizRepository
                .findFirstByProfessionIdAndStatusIsNot(professionId, Status.INACTIVE);
        Quiz quizRecord;
        if (!quiz.isPresent()) {
            throw new QuizNotFoundException("No quiz found for profession");
        } else {
            quizRecord = quiz.get();
        }

        Question question = questionDTOFactory.createEntity(questionDTO);
        question.setQuiz(quizRecord);
        if (questionDTO.getAnswer().getCorrectAnswers().length > 1) {
            question.setType(QuestionType.MULTIPLE);
        } else {
            question.setType(QuestionType.SINGLE);
        }
        Answer answer = answerDtoFactory.createEntity(questionDTO.getAnswer());
        answer.setQuestion(question);
        question.setAnswer(answer);
        question.setStatus(Status.ACTIVE);
        quizRecord.getQuestions().add(question);
        quizRecord.setNumberOfQuestions(quizRecord.getQuestions().size());
        return quizRepository.save(quizRecord).getId();
    }

    public void deleteQuestion(Long id) {
        Optional<Question> optionalQuestion = questionRepository.findById(id);
        if (!optionalQuestion.isPresent()) {
            throw new EntityNotFoundException("No question found matching the id");
        } else {
            Question question = optionalQuestion.get();
            question.setStatus(Status.INACTIVE);
            int numberOfQuestions = question.getQuiz().getNumberOfQuestions();
            question.getQuiz().setNumberOfQuestions(--numberOfQuestions);
            questionRepository.save(question);
        }
    }

    public Optional<QuestionDto> getById(Long id) {
        Optional<Question> optionalQuestion = questionRepository.findById(id);
        if (!optionalQuestion.isPresent()) {
            throw new EntityNotFoundException("No question found matching the id");
        }

        return Optional.of(questionDTOFactory.create(optionalQuestion.get()));
    }
}
