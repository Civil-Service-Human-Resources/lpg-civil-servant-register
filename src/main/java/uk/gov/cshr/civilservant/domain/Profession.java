package uk.gov.cshr.civilservant.domain;

import java.util.Collections;
import java.util.List;
import javax.persistence.Entity;

import lombok.Builder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Builder
@Entity
public class Profession extends SelfReferencingEntity<Profession> {
    public Profession() {
    }

    public Profession(String name) {
        this.name = name;
    }

    @Override
    public Profession getParent() {
        return parent;
    }

    @Override
    public void setParent(Profession parent) {
        this.parent = parent;
    }

    @Override
    public void setChildren(List<Profession> children) {
        this.children = Collections.unmodifiableList(children);
    }

    @Override
    public List<Profession> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public boolean hasParent() {
        return parent != null;
    }

    @Override
    public boolean hasChildren() {
        return !children.isEmpty();
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
                .append("parent", parent)
                .toString();
    }
}
