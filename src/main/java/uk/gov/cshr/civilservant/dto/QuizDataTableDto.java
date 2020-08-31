package uk.gov.cshr.civilservant.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class QuizDataTableDto {
  private String profession;
  private int numberOfAttempts;
  private String averageScore;
}
