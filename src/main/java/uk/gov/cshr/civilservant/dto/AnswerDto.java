package uk.gov.cshr.civilservant.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnswerDto {
  private Long id;
  private String[] correctAnswers;
  private Map<String, String> answers;
}
