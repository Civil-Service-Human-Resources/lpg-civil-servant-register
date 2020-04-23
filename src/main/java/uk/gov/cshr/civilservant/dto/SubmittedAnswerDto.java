package uk.gov.cshr.civilservant.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SubmittedAnswerDto {
    int questionId;
    String[] submittedAnswers;
    boolean skipped;
    QuizResultQuestionDto question;
}
