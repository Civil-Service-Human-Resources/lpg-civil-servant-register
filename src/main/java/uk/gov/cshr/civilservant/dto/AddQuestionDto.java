package uk.gov.cshr.civilservant.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddQuestionDto {
    private Integer professionId;
    private QuestionDto question;
}
