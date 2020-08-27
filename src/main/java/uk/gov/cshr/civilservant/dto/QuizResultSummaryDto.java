package uk.gov.cshr.civilservant.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class QuizResultSummaryDto {
  private long professionId;
  private long numberOfAttempts;
  private Double averageScore;

  public QuizResultSummaryDto(long professionId, long numberOfAttempts, Double averageScore) {
    this.professionId = professionId;
    this.averageScore = averageScore;
    this.numberOfAttempts = numberOfAttempts;
  }
}
