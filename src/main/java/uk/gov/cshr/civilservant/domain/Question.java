package uk.gov.cshr.civilservant.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private QuestionType type;

    @Column(nullable = false)
    private String value;

    @ManyToMany
    private Set<Choice> choices = new HashSet<>();

    @ManyToMany
    private Set<Choice> answers = new HashSet<>();
}
