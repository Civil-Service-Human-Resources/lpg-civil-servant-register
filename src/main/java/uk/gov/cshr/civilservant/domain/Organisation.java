package uk.gov.cshr.civilservant.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.unmodifiableList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Entity
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String code;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "organisation")
    private List<Grade> grades = new ArrayList<>();

    @ManyToOne(optional = false)
    private Department department;

    protected Organisation() {
    }

    public Organisation(String code, String name) {
        setCode(code);
        setName(name);
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        checkArgument(isNotEmpty(code));
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        checkArgument(isNotEmpty(name));
        this.name = name;
    }

    public List<Grade> getGrades() {
        return unmodifiableList(grades);
    }

    public Department getDepartment() {
        return department;
    }

    public void addGrade(Grade grade) {
        checkArgument(grade != null);
        grades.add(grade);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Organisation that = (Organisation) o;

        return new EqualsBuilder()
                .append(code, that.code)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(code)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("code", code)
                .append("name", name)
                .append("grades", grades)
                .append("department", department)
                .toString();
    }
}
