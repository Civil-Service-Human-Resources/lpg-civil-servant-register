package uk.gov.cshr.civilservant.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.opencsv.bean.CsvBindByName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uk.gov.cshr.civilservant.domain.Status;

@Builder
@Getter
@Setter
@JsonPropertyOrder({
  "quizName",
  "status",
  "professionName",
  "questionId",
  "questionName",
  "questionTheme",
  "timesAttempted",
  "correctCount",
  "incorrectCount",
  "skippedCount"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SkillsReportsDto {
  @CsvBindByName(column = "Quiz Name")
  private String quizName;

  @CsvBindByName(column = "Quiz Status")
  private Status status;

  @CsvBindByName(column = "Profession Name")
  private String professionName;

  @CsvBindByName(column = "Question Id")
  private long questionId;

  @CsvBindByName(column = "Question text")
  private String questionName;

  @CsvBindByName(column = "Question Theme")
  private String questionTheme;

  @CsvBindByName(column = "Times Attempted")
  private long timesAttempted;

  @CsvBindByName(column = "Correct Count")
  private long correctCount;

  @CsvBindByName(column = "Incorrect Count")
  private long incorrectCount;

  @CsvBindByName(column = "Skipped Count")
  private long skippedCount;

  public SkillsReportsDto(
      String quizName,
      Status status,
      String professionName,
      long questionId,
      String questionName,
      String questionTheme,
      long timesAttempted,
      long correctCount,
      long incorrectCount,
      long skippedCount) {
    this.quizName = quizName;
    this.status = status;
    this.professionName = professionName;
    this.questionId = questionId;
    this.questionName = questionName;
    this.questionTheme = questionTheme;
    this.timesAttempted = timesAttempted;
    this.correctCount = correctCount;
    this.incorrectCount = incorrectCount;
    this.skippedCount = skippedCount;
  }
}
