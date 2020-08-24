package uk.gov.cshr.civilservant.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionMetrics {
    private int correctCount;
    private int timesAttempted;
    private int incorrectCount;
    private int skippedCount;
}
