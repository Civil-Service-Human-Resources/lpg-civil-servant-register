package uk.gov.cshr.civilservant.dto.factory;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.cshr.civilservant.domain.Question;
import uk.gov.cshr.civilservant.dto.AnswerDto;
import uk.gov.cshr.civilservant.dto.QuestionDto;

@Component
public class QuestionDtoFactory extends DtoFactory<QuestionDto, Question> {

    ModelMapper modelMapper;
    AnswerDtoFactory answerDtoFactory;

    @Autowired
    public QuestionDtoFactory(ModelMapper modelMapper,
                              AnswerDtoFactory answerDtoFactory) {
        this.answerDtoFactory = answerDtoFactory;
        this.modelMapper = modelMapper;
    }

    @Override
    public QuestionDto create(Question question) {
        QuestionDto questionDto = modelMapper.map(question, QuestionDto.class);
        AnswerDto answerDto = answerDtoFactory.create(question.getAnswer());
        questionDto.setAnswer(answerDto);
        return questionDto;
    }

    public Question createEntity(QuestionDto questionDTO) {
        return modelMapper.map(questionDTO, Question.class);
    }
}
