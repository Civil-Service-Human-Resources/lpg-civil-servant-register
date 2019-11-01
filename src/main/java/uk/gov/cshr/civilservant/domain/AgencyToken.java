package uk.gov.cshr.civilservant.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import uk.gov.cshr.civilservant.validation.ValidCapacityUsed;

import javax.persistence.*;
import java.util.Set;

@ValidCapacityUsed(
        capacity = "capacity",
        capacityUsed = "capacityUsed"
)
@Data
@Entity
public class AgencyToken implements RegistryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(nullable = false, length = 20, unique = true)
    private String token;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private int capacityUsed;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    private Set<AgencyDomain> agencyDomains;

    public AgencyToken() {
    }

    public AgencyToken(long id, String token, int capacity, int capacityUsed) {
        this.id = id;
        this.token = token;
        this.capacity = capacity;
        this.capacityUsed = capacityUsed;
    }
}
