package uk.gov.cshr.civilservant.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class QuizSubmissionDto {
  String staffId;
  int quizId;
  int professionId;
  int organisationId;
  String quizName;
  SubmittedAnswerDto[] answers;
}
