package uk.gov.cshr.civilservant.dto.factory;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.cshr.civilservant.domain.Quiz;
import uk.gov.cshr.civilservant.dto.QuizDto;

@Component
public class QuizDtoFactory extends DtoFactory<QuizDto, Quiz> {

  ModelMapper modelMapper;

  @Autowired
  public QuizDtoFactory(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  @Override
  public QuizDto create(Quiz quiz) {
    return modelMapper.map(quiz, QuizDto.class);
  }

  public Quiz mapDtoToModel(QuizDto quizDTO) {
    return modelMapper.map(quizDTO, Quiz.class);
  }

  public QuizDto mapSpecificFields(Quiz quiz) {
    return QuizDto.builder()
        .id(quiz.getId())
        .name(quiz.getName())
        .description(quiz.getDescription())
        .status(quiz.getStatus())
        .numberOfQuestions(quiz.getNumberOfQuestions())
        .build();
  }
}
