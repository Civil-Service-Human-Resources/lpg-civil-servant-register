package uk.gov.cshr.civilservant.dto.factory;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.cshr.civilservant.domain.QuizResult;
import uk.gov.cshr.civilservant.domain.SubmittedAnswer;
import uk.gov.cshr.civilservant.dto.QuizResultDto;
import uk.gov.cshr.civilservant.dto.QuizResultQuestionDto;
import uk.gov.cshr.civilservant.dto.SubmittedAnswerDto;

@Component
public class QuizResultDtoFactory extends DtoFactory<QuizResultDto, QuizResult> {

    ModelMapper modelMapper;
    QuestionDtoFactory questionDtoFactory;

    @Autowired
    public QuizResultDtoFactory (ModelMapper modelMapper, QuestionDtoFactory questionDtoFactory) {
        this.modelMapper = modelMapper;
        this.questionDtoFactory = questionDtoFactory;
    }

    @Override
    public QuizResultDto create(QuizResult entity) {
        QuizResultDto quizResultDto = modelMapper.map(entity, QuizResultDto.class);
        List<SubmittedAnswer> submittedAnswerRecords = entity.getAnswers();
        List<SubmittedAnswerDto> submittedAnswers = new ArrayList<>();
        submittedAnswerRecords.forEach(submittedAnswer -> {
            QuizResultQuestionDto quizResultQuestionDto = modelMapper.map(questionDtoFactory.create(submittedAnswer.getQuestion()), QuizResultQuestionDto.class);
            submittedAnswers.add(new SubmittedAnswerDto(
                    (int) submittedAnswer.getId(),
                    submittedAnswer.getSubmittedAnswers(),
                    submittedAnswer.isSkipped(),
                    quizResultQuestionDto));
        });
        quizResultDto.setAnswers(submittedAnswers);
        return quizResultDto;
    }
}
