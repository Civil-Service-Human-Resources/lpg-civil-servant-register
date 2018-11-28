package uk.gov.cshr.civilservant.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
public abstract class SelfReferencingEntity<T> implements RegistryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true, nullable = false)
    String name;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonBackReference
    T parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST)
    List<T> children = new ArrayList<>();

    public abstract T getParent();

    public abstract void setParent(T parent);

    public abstract void setChildren(List<T> children);

    public abstract List<T> getChildren();

    public boolean hasParent() {
        return parent != null;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
