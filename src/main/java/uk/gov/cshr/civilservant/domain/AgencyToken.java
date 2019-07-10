package uk.gov.cshr.civilservant.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class AgencyToken implements RegistryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(nullable = false)
    private String domain;

    @Column(nullable = false, length = 20, unique = true)
    private String token;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private int tokensUsed;

    @OneToOne
    private OrganisationalUnit organisationalUnit;
}
