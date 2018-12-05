package uk.gov.cshr.civilservant.service;

import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.SelfReferencingEntity;
import uk.gov.cshr.civilservant.dto.DtoFactory;
import uk.gov.cshr.civilservant.repository.SelfReferencingEntityRepository;

import java.util.List;
import java.util.stream.Collectors;

public abstract class SelfReferencingEntityService<T extends SelfReferencingEntity, K> {
    private final DtoFactory<K, T> dtoFactory;
    private SelfReferencingEntityRepository<T> repository;
    private RepositoryEntityService<T> repositoryEntityService;

    SelfReferencingEntityService(SelfReferencingEntityRepository<T> repository, RepositoryEntityService<T> repositoryEntityService, DtoFactory<K, T> dtoFactory) {
        this.repository = repository;
        this.repositoryEntityService = repositoryEntityService;
        this.dtoFactory = dtoFactory;
    }

    /**
     * This will return all parent entities with any children as a list
     */
    @Transactional(readOnly = true)
    public List<T> getParents() {
        return repository.findAllByOrderByNameAsc()
                .stream()
                .filter(org -> !org.hasParent())
                .collect(Collectors.toList());
    }

    /**
     * This will return all entities as a map.
     * In the map, the key will be the href of the entity which is obtained using {@link RepositoryEntityLinks} in {@link RepositoryEntityService}.
     * The value will represent the entity name, but formatted to include any parents recursively using {@link SelfReferencingEntityService#formatName(T)}.
     * Finally, this is map is sorted by value into a LinkedHashMap
     */
    @Transactional(readOnly = true)
    public List<K> getListSortedByValue() {
        return repository.findAll().stream()
                .map(o -> dtoFactory.create(o)).collect(Collectors.toList());
//                .collect(Collectors.toMap(entity -> dtoFactory.create(entity), this::formatName))
//                .entrySet()
//                .stream()
//                .sorted(Comparator.comparing(Map.Entry::getValue))
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
//                        (u, v) -> {
//                            throw new IllegalStateException(String.format("Duplicate key %s", u));
//                        },
//                        LinkedHashMap::new));
    }

    /**
     * Format the name of an Entity to be prefixed with parental hierarchy.
     * <p>
     * e.g. Parent | Child | Subchild
     */
    abstract String formatName(T entity);
}
