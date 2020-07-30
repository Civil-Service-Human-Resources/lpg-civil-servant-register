package uk.gov.cshr.civilservant.dto.factory;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.cshr.civilservant.domain.Answer;
import uk.gov.cshr.civilservant.dto.AnswerDto;

@Component
public class AnswerDtoFactory extends DtoFactory<AnswerDto, Answer> {
  @Autowired ModelMapper modelMapper;

  @Override
  public AnswerDto create(Answer answer) {
    String[] correctAnswers = answer.getCorrectAnswer().split(",");
    AnswerDto answerDto = modelMapper.map(answer, AnswerDto.class);
    answerDto.setCorrectAnswers(correctAnswers);
    return answerDto;
  }

  public Answer createEntity(AnswerDto answerDTO) {
    String correctAnswers = String.join(",", answerDTO.getCorrectAnswers());
    Answer answer = modelMapper.map(answerDTO, Answer.class);
    answer.setCorrectAnswer(correctAnswers);
    return answer;
  }
}
