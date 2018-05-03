package uk.gov.cshr.civilservant.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.rest.core.config.Projection;

import javax.persistence.*;

import static com.google.common.base.Preconditions.checkArgument;

@Entity
public class CivilServant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @ManyToOne
    private Organisation organisation;

    @ManyToOne
    private Grade grade;

    @OneToOne
    @JsonIgnore
    private Identity identity;

    protected CivilServant() {
    }

    public CivilServant(Identity identity) {
        checkArgument(identity != null);
        this.identity = identity;
    }

    public Long getId() {
        return id;
    }

    public Identity getIdentity() {
        return identity;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CivilServant that = (CivilServant) o;

        return new EqualsBuilder()
                .append(identity, that.identity)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(identity)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("organisation", organisation)
                .append("grade", grade)
                .append("identity", identity)
                .toString();
    }
}