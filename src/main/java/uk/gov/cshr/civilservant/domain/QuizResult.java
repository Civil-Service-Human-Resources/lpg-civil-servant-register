package uk.gov.cshr.civilservant.domain;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.*;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class QuizResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "staff_id")
    public String staffId;

    @Column(name = "quiz_id")
    public long quizId;

    @Column(name = "quiz_name")
    String quizName;

    @Column(name = "profession_id")
    public long professionId;

    @Column(name = "organisation_id")
    public long organisationId;

    @Column
    @Enumerated(EnumType.STRING)
    public QuizType type;

    @Column
    @Enumerated(EnumType.STRING)
    public Result result;

    @CreationTimestamp
    private LocalDateTime completedOn;

    @Column(name = "correct_count")
    int correctAnswers;

    @Column(name = "question_count")
    int numberOfQuestions;

    @Column(name = "score_obtained")
    float score;

    @OneToMany(mappedBy = "quizResult",fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<SubmittedAnswer> answers;

}
