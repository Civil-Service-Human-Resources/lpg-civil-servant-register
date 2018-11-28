package uk.gov.cshr.civilservant.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Profession extends SelfReferencingEntity<Profession> {
    public Profession() {
    }

    public Profession(String name) {
        this.name = name;
    }

    public Profession getParent() {
        return parent;
    }

    public void setParent(Profession parent) {
        this.parent = parent;
    }

    public void setChildren(List<Profession> children) {
        this.children = Collections.unmodifiableList(children);
    }

    public List<Profession> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public boolean hasParent() {
        return parent != null;
    }

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
