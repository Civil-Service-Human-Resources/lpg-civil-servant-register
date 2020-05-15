package uk.gov.cshr.civilservant.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
public class AgencyToken implements RegistryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(nullable = false, length = 20, unique = true)
    private String token;

    @Column(unique = true, length = 36)
    private String uid;

    @Column(nullable = false)
    private int capacity;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    private Set<AgencyDomain> agencyDomains;

    public AgencyToken() {
    }

    public AgencyToken(long id, String token, int capacity, String uid) {
        this.id = id;
        this.token = token;
        this.capacity = capacity;
        this.uid = uid;
    }
}
