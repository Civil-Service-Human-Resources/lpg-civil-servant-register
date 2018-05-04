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
public class JobRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    private Profession profession;

    @ManyToOne
    private JobRole parent;

    @OneToMany(mappedBy = "parent")
    private List<JobRole> children = new ArrayList<>();

    protected JobRole() {
    }

    public JobRole(String name) {
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

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public JobRole getParent() {
        return parent;
    }

    public void setParent(JobRole parent) {
        this.parent = parent;
    }

    public List<JobRole> getChildren() {
        return unmodifiableList(children);
    }

    public void setChildren(List<JobRole> children) {
        this.children.clear();
        if (children != null) {
            this.children.addAll(children);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        JobRole jobRole = (JobRole) o;

        return new EqualsBuilder()
                .append(name, jobRole.name)
                .append(profession, jobRole.profession)
                .append(parent, jobRole.parent)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .append(profession)
                .append(parent)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("profession", profession)
                .append("parent", parent)
                .toString();
    }
}
