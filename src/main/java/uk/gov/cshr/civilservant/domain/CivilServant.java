package uk.gov.cshr.civilservant.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import uk.gov.cshr.civilservant.service.identity.IdentityFromService;

import javax.persistence.*;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.unmodifiableSet;

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

    @ManyToOne
    private Profession profession;

    @ManyToOne
    private JobRole jobRole;

    @OneToOne
    @JsonIgnore
    private Identity identity;

    @ManyToMany
    private Set<Profession> otherAreasOfWork = new HashSet<>();


    private String lineManagerUid;

    private String lineManagerEmail;

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

    public String getLineManagerUid() {
        return lineManagerUid;
    }

    public void setLineManagerUid(String lineManager) {
        this.lineManagerUid =lineManager;
    };

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

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public JobRole getJobRole() {
        return jobRole;
    }

    public void setJobRole(JobRole jobRole) {
        this.jobRole = jobRole;
    }

    public Set<Profession> getOtherAreasOfWork() {
        return unmodifiableSet(otherAreasOfWork);
    }

    public void setOtherAreasOfWork(Set<Profession> otherAreasOfWork) {
        this.otherAreasOfWork.clear();
        if (otherAreasOfWork != null) {
            this.otherAreasOfWork.addAll(otherAreasOfWork);
        }
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
                .append("profession", profession)
                .append("jobRole", jobRole)
                .append("otherAreasOfWork", otherAreasOfWork)
                .append("identity", identity)
                .toString();
    }


    public String getLineManagerEmail() {
        return lineManagerEmail;
    }

    public void setLineManagerEmail(String lineManagerEmail) {
        this.lineManagerEmail = lineManagerEmail;
    }
}