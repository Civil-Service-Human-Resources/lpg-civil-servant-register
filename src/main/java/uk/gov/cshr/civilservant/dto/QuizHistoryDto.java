package uk.gov.cshr.civilservant.dto;

import java.util.List;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizHistoryDto {
  private List<QuizResultDto> quizResultDto;

  public void setQuizResultDto(List<QuizResultDto> quizResultDto) {
    this.quizResultDto = quizResultDto;
  }
}
