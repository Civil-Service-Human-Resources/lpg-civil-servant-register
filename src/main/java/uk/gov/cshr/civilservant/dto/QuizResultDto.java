package uk.gov.cshr.civilservant.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.*;
import uk.gov.cshr.civilservant.domain.QuizType;
import uk.gov.cshr.civilservant.domain.Result;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class QuizResultDto {
    private long id;
    private String staffId;
    private long quizId;
    private String quizName;
    private long professionId;
    private long organisationId;
    private QuizType type;
    private Result result;
    private LocalDateTime completedOn;
    private int correctAnswers;
    private int numberOfQuestions;
    private float score;
    private List<SubmittedAnswerDto> answers;

    public QuizResultDto(long id,
                         String staffId,
                         long quizId,
                         String quizName,
                         long professionId,
                         long organisationId,
                         QuizType type,
                         Result result,
                         int correctAnswers,
                         int numberOfQuestions,
                         float score,
                         LocalDateTime completedOn){
        this.id = id;
        this.staffId = staffId;
        this.quizId = quizId;
        this.quizName = quizName;
        this.professionId = professionId;
        this.organisationId = organisationId;
        this.type = type;
        this.result = result;
        this.correctAnswers = correctAnswers;
        this.numberOfQuestions = numberOfQuestions;
        this.score = score;
        this.completedOn = completedOn;
    }

    public QuizResultDto(long id,
                         int correctAnswers,
                         int numberOfQuestions,
                         float score,
                         LocalDateTime completedOn){
        this.id = id;
        this.correctAnswers = correctAnswers;
        this.numberOfQuestions = numberOfQuestions;
        this.score = score;
        this.completedOn = completedOn;
    }

    public QuizResultDto(long id,
                         long professionId,
                         int correctAnswers,
                         int numberOfQuestions,
                         float score,
                         LocalDateTime completedOn){
        this.id = id;
        this.professionId = professionId;
        this.correctAnswers = correctAnswers;
        this.numberOfQuestions = numberOfQuestions;
        this.score = score;
        this.completedOn = completedOn;
    }
}
