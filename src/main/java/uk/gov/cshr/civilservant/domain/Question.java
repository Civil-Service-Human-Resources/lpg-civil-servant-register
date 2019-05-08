package uk.gov.cshr.civilservant.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Enumerated(EnumType.STRING)
    private QuestionType type;

    @Column(nullable = false, length = 500)
    private String learningName;

    @Column(nullable = false, length = 500)
    private String learningReference;

    @Column(nullable = false)
    private String value;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Choice> choices = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Choice> answers = new HashSet<>();
}
