package uk.gov.cshr.civilservant.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Profession profession;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Question> questions = new HashSet<>();
}
