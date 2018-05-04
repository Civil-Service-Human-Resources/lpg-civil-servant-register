package uk.gov.cshr.civilservant.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.unmodifiableList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Entity
public class Profession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "profession")
    private List<JobRole> jobRoles = new ArrayList<>();

    protected Profession() {
    }

    public Profession(String name) {
        setName(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        checkArgument(isNotBlank(name));
        this.name = name;
    }

    public List<JobRole> getJobRoles() {
        return unmodifiableList(jobRoles);
    }

    public void setJobRoles(List<JobRole> jobRoles) {
        this.jobRoles.clear();
        if (jobRoles != null) {
            this.jobRoles.addAll(jobRoles);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Profession that = (Profession) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("jobRoles", jobRoles)
                .toString();
    }
}
