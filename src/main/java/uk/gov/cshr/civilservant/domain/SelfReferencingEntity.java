package uk.gov.cshr.civilservant.domain;

import java.util.List;
import java.util.Optional;

public interface SelfReferencingEntity<T> extends RegistryEntity {
    Optional<T> getParent();

    boolean hasParent();

    List<T> getChildren();

    boolean hasChildren();
}
