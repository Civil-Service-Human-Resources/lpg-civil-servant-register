package uk.gov.cshr.civilservant.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Choice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String value;
}
