package uk.gov.cshr.civilservant.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import uk.gov.cshr.civilservant.domain.QuestionType;
import uk.gov.cshr.civilservant.domain.Status;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionDto {
    private Long id;
    private QuestionType type;
    private String theme;
    private String value;
    private String why;
    private AnswerDto answer;
    private String imgUrl;
    private String suggestions;
    private Status status;
    private String alternativeText;
    private String learningName;
    private String learningReference;
}
