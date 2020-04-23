package uk.gov.cshr.civilservant.dto;

import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import uk.gov.cshr.civilservant.domain.Profession;
import uk.gov.cshr.civilservant.domain.Status;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuizDto {
  private Long id;
  private String name;
  private Profession profession;
  private long organisationId;
  private Set<QuestionDto> questions;
  private LocalDateTime createdOn;
  private LocalDateTime updatedOn;
  private String result;
  private Status status;
  private int numberOfQuestions;
  private String description;
  private int numberOfAttempts;
  private float averageScore;
}
