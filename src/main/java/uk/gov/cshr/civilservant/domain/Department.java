package uk.gov.cshr.civilservant.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.unmodifiableCollection;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Entity
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany
    private Collection<Organisation> organisations = new HashSet<>();

    protected Department() {
    }

    public Department(String code, String name) {
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

    public Collection<Organisation> getOrganisations() {
        return unmodifiableCollection(organisations);
    }

    public void addOrganisation(Organisation organisation) {
        checkArgument(organisation != null);
        organisations.add(organisation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Department that = (Department) o;

        return new EqualsBuilder()
                .append(code, that.code)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(code)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("code", code)
                .append("name", name)
                .append("organisations", organisations)
                .toString();
    }
}
